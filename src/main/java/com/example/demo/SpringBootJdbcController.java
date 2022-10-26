package com.example.demo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.ConferenceData;
import com.google.api.services.calendar.model.ConferenceSolution;
import com.google.api.services.calendar.model.ConferenceSolutionKey;
import com.google.api.services.calendar.model.CreateConferenceRequest;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

@Controller
public class SpringBootJdbcController {

	private static final String APPLICATION_NAME = "CalendarTest";
	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	/** Directory to store authorization tokens for this application. */
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	/**
	 * Global instance of the scopes required by this quickstart. If modifying these
	 * scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

	private static final String SUMMARY = "IT Patch Management";
	private static final String LOCATION = "800 Howard St., San Francisco, CA 94103";
	private static final String DESCRIPTION = "1Great chance to resolve IT issues seamlessly!";
	private static final String RECURRENTCE_RULE = "RRULE:FREQ=DAILY;COUNT=1";
	private static final String calendarId = "primary";
	private static final int DAY_START_TIME = 9;
	private static final int DAY_END_TIME = 17;
	private static final String DAY_TYPE_WEEKDAY = "WEEKDAY";
	private static final String DAY_TYPE_WEEKEND = "WEEKEND";
	private static final String DAY_SLOT_START_TIME = "09:00";
	private static final String DAY_SLOT_END_TIME = "17:00";
	private static final String SCOPE_IDENTITY_QUERY = "SELECT SCOPE_IDENTITY()";

	long no_of_days = 0;
	String[] slot_start_date_time = null, slot_end_date_time = null;

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	HomeController homeController;

	@RequestMapping("/insert")
	public String index() {
		jdbc.execute("insert into user(name,email)values('javatpoint','java@javatpoint.com')");
		return "data inserted Successfully";
	}
//create table slots(id int UNSIGNED primary key not null auto_increment, slot_start timestamp, slot_end timestamp, time_zone varchar(100), it_email_list varchar(1000), attendee_email_list varchar(1000), region varchar(100),is_booked boolean);  

	/**
	 * @param slot_start
	 * @param slot_end
	 * @param it_email_list
	 * @param attendee_email_list
	 * @param region
	 * @param is_booked
	 * @param time_zone
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/insertslot")
	@ResponseBody
	public String insertslot(String slot_start, String slot_end, String it_email_list, String attendee_email_list,
			String region, boolean is_booked, String time_zone) throws ParseException {

		String query1 = "insert into slots(slot_start,slot_end, time_zone, it_email_list,attendee_email_list,region,is_booked) values('2022-08-09 07:30:30','2022-08-09 08:30:30', 'EST','saurabh.gupta1@hcl.com','saurabh22045@gmail.com', 'Spain', 0)";

		String query2 = "insert into slots(slot_start,slot_end, time_zone, it_email_list,attendee_email_list,region,is_booked) values('"
				+ slot_start + "','" + slot_end + "', '" + time_zone + "','" + it_email_list + "','"
				+ attendee_email_list + "', '" + region + "', " + BooleanUtils.toInteger(is_booked) + ")";

		jdbc.execute(query2);
		return "slot created Successfully";
	}

	/**
	 * @param slot_start
	 * @param slots_end
	 * @param slot_duration
	 * @param it_email_list
	 * @param attendee_email_list
	 * @param region
	 * @param is_booked
	 * @param time_zone
	 * @param include_weekends
	 * @param holidays
	 * @param create_event
	 * @return
	 * @throws ParseException
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	@RequestMapping("/insertslots")
	@ResponseBody
	public String insertslots(String slot_start, String slots_end, String slot_duration, String it_email_list,
			String attendee_email_list, String region, boolean is_booked, String time_zone, boolean include_weekends,
			String holidays, String create_event) throws ParseException, IOException, GeneralSecurityException {

		getSlotDurationDays(slot_start, slots_end);
		String slot_end = "";

		String query1 = "insert into slots(slot_start,slot_end, time_zone, it_email_list,attendee_email_list,region,is_booked) values('2022-08-09 07:30:30','2022-08-09 08:30:30', 'EST','saurabh.gupta1@hcl.com','saurabh22045@gmail.com', 'Spain', 0)";

		String startDate = slot_start_date_time[0];
		String currentSlotStartDate, currentSlotStartTime;
		String currentDate;
		currentDate = startDate;
		String query2 = "";

		int totalSlotsPerDay = ((DAY_END_TIME - DAY_START_TIME) * 60) / Integer.parseInt(slot_duration);
		// int totalSlots = (slots_end - slot_start)/slot_duration;
		// String[] slot_start_date_time = null, slot_end_date_time = null;

		int i = 0;
		int j = 0;
		currentSlotStartDate = slot_start_date_time[0];
		currentSlotStartTime = slot_start_date_time[1];
		// LocalDateTime dateTime = LocalDateTime.parse(slot_start);
		String slot_start_tmp = slot_start;

		while (i <= no_of_days) {
			j = 0;
			while (j < totalSlotsPerDay) {
				if (!StringUtils.contains(holidays, currentDate)) {
					if (dayType(currentDate).equalsIgnoreCase(DAY_TYPE_WEEKDAY)
							|| (dayType(currentDate).equalsIgnoreCase(DAY_TYPE_WEEKEND) && include_weekends)) {
						LocalDateTime dateTime = LocalDateTime.parse(slot_start_tmp);
						slot_end = dateTime.plusMinutes(Long.parseLong(slot_duration)).toString();

						if (StringUtils.isBlank(attendee_email_list)) {
							attendee_email_list = it_email_list;
						}
						query2 = "insert into slots(slot_start,slot_end, time_zone, it_email_list,attendee_email_list,region,is_booked) values('"
								+ slot_start_tmp + "','" + slot_end + "', '" + time_zone + "','" + it_email_list + "','"
								+ attendee_email_list + "', '" + region + "', " + BooleanUtils.toInteger(is_booked)
								+ ")";
						// jdbc.execute(query2);

						final String SQL = query2;
						KeyHolder keyHolder = new GeneratedKeyHolder();
						jdbc.update(connection -> {
							PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
							return ps;
						}, keyHolder);

						int requestId = keyHolder.getKey().intValue();

						System.err.print("Key--->" + requestId);
						SlotModel currentSlot = findSingleSlot(requestId);

						if (Boolean.TRUE.toString().equalsIgnoreCase(create_event))
							homeController.bookSlotAndCreateEvent(currentSlot.getSlot_start(),
									currentSlot.getSlot_end(), attendee_email_list, requestId, time_zone,
									Boolean.toString(is_booked));
					}
				}
				j++;
				slot_start_tmp = slot_end;
			}
			LocalDate dateTime = LocalDate.parse(slot_start_date_time[0]);
			currentDate = dateTime.plusDays(i + 1).toString();
			slot_start_tmp = currentDate + "T" + DAY_SLOT_START_TIME;
			i++;
		}

		return "slots created Successfully";
	}

	public String dayType(String slot_start_date) {
		String dayType;
		LocalDate date = LocalDate.parse(slot_start_date);
		if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
			dayType = DAY_TYPE_WEEKEND;
		} else {
			dayType = DAY_TYPE_WEEKDAY;
		}
		return dayType;
	}

	/**
	 * @param slotId
	 * @param attendee_email_list
	 * @param is_booked
	 * @param eventLink
	 * @return
	 */
	// UPDATE springbootdb.slots SET
	// attendee_email_list='saurabh.gupta@hcl.com',is_booked=false,event_link='https://www.google.com/calendar/event?eid=N2JxZnNrNHEycWZmcDRicmViOHIzdWJxZDhfMjAyMjA4MTFUMDYzMDMwWiBzYXVyYWJoMjIwNEBt'
	// WHERE id = 1;
	@RequestMapping("/updateslot")
	@ResponseBody
	public String updateSlot(int slotId, String attendee_email_list, String is_booked, String eventLink) {

		int isBooked = BooleanUtils.toInteger(Boolean.parseBoolean(is_booked));

		String query = "UPDATE springbootdb.slots SET attendee_email_list=" + "'" + attendee_email_list + "'"
				+ ",is_booked=" + isBooked + ",event_link=" + "'" + eventLink + "'" + " WHERE id = " + slotId + ";";
		jdbc.execute(query);
		return "slot updated Successfully";
	}

	public List<SlotModel> findAllSlots(String[] filterArr) {
//SELECT * FROM springbootdb.slots where slot_start>='2022-08-31 11:00:00' and slot_end <= '2022-09-01 14:00:00';
		String sql = "SELECT * FROM slots";

		if (StringUtils.isNotBlank(filterArr[0]))
			sql = sql + " where slot_start>='" + filterArr[0] + "'";
		if (StringUtils.isNotBlank(filterArr[0]) && StringUtils.isNotBlank(filterArr[1]))
			sql = sql + " and slot_end<='" + filterArr[1] + "'";
		if (StringUtils.isBlank(filterArr[0]) && StringUtils.isNotBlank(filterArr[1]))
			sql = sql + " where slot_end<='" + filterArr[1] + "'";

		List<SlotModel> slots = new ArrayList<>();

		List<Map<String, Object>> rows = jdbc.queryForList(sql);
		String tz_Tmp, region_tmp;
		boolean isBookTmp;
		for (Map row : rows) {

			SlotModel obj = new SlotModel();
			obj.setId(((Long) row.get("id")));
			obj.setSlot_start(((Timestamp) row.get("slot_start")).toString());
			obj.setSlot_end(((Timestamp) row.get("slot_end")).toString());
			tz_Tmp = ((String) row.get("time_zone")).toString();
			obj.setTime_zone(tz_Tmp);
			obj.setIt_email_list(((String) row.get("it_email_list")).toString());
			obj.setAttendee_email_list(((String) row.get("attendee_email_list")).toString());
			region_tmp = ((String) row.get("region")).toString();
			obj.setRegion(region_tmp);
			isBookTmp = ((boolean) row.get("is_booked"));
			obj.setIs_booked(isBookTmp);

			if (StringUtils.isNotBlank(filterArr[2]) && StringUtils.equalsIgnoreCase(filterArr[2].trim(), "GMT")) {
				System.err.println("GMT");
			}
			if (StringUtils.isNotBlank(filterArr[2])
					&& !StringUtils.equalsIgnoreCase(filterArr[2].trim(), tz_Tmp.trim()))
				continue;

			if (StringUtils.isNotBlank(filterArr[3])
					&& !StringUtils.equalsIgnoreCase(filterArr[3].trim(), region_tmp.trim()))
				continue;

			if (StringUtils.isNotBlank(filterArr[4]) && BooleanUtils.toBoolean(filterArr[4].trim()) == isBookTmp)
				continue;

			slots.add(obj);
		}

		return slots;
	}

	public SlotModel findSingleSlot(int requestId) {

		String sql = "SELECT * FROM slots where id=" + requestId;

		List<SlotModel> slots = new ArrayList<>();

		List<Map<String, Object>> rows = jdbc.queryForList(sql);

		for (Map row : rows) {
			SlotModel obj = new SlotModel();
			obj.setId(((Long) row.get("id")));

			obj.setSlot_start(((Timestamp) row.get("slot_start")).toString());
			obj.setSlot_end(((Timestamp) row.get("slot_end")).toString());
			obj.setTime_zone(((String) row.get("time_zone")).toString());
			obj.setIt_email_list(((String) row.get("it_email_list")).toString());
			obj.setAttendee_email_list(((String) row.get("attendee_email_list")).toString());
			obj.setRegion(((String) row.get("region")).toString());

			obj.setIs_booked(((boolean) row.get("is_booked")));

			slots.add(obj);
		}

		return slots.get(0);
	}

	public String findPassword(String user) {
		String password = "";
		String sql = "SELECT password FROM user where name=" + "'" + user + "'";

		List<SlotModel> slots = new ArrayList<>();

		List<Map<String, Object>> rows = jdbc.queryForList(sql);

		for (Map row : rows) {
			password = (String) row.get("password");

		}

		return password;
	}

	public List<MedicineModel> getMedicines() {
		int Id;
		String name;
		String manufacturers;
		String salt_composition;
		String medicine_type;
		String stock;
		String introduction;
		String benefits;
		String description;
		String how_to_use;
		String safety_advise;
		String if_miss;
		String ingredients;
		String packaging;
		String packaging_typ;
		String mrp;
		String prescription_required;
		String label;
		String Fact_Box;
		String primary_use;
		String storage;
		String common_side_effect;
		String alcoholInteraction;
		String pregnancyInteraction;

		String password = "";
		String sql = "SELECT * FROM medicine";

		List<MedicineModel> medicines = new ArrayList<>();

		List<Map<String, Object>> rows = jdbc.queryForList(sql);

		for (Map row : rows) {
			MedicineModel obj = new MedicineModel();

			Id = (Integer) row.get("Id"); obj.setId(Id);
			name = (String) row.get("name"); obj.setName(name);
			manufacturers = (String) row.get("manufacturers");obj.setManufacturers(manufacturers);
			salt_composition = (String) row.get("salt_composition");obj.setSalt_composition(salt_composition);
			medicine_type = (String) row.get("medicine_type");obj.setMedicine_type(medicine_type);
			stock = (String) row.get("stock");obj.setStock(stock);
			introduction = (String) row.get("introduction");obj.setIntroduction(introduction);
			benefits = (String) row.get("benefits");obj.setBenefits(benefits);
			description = (String) row.get("description");obj.setDescription(description);
			how_to_use = (String) row.get("how_to_use");obj.setHow_to_use(how_to_use);
			safety_advise = (String) row.get("safety_advise");obj.setSafety_advise(safety_advise);
			if_miss = (String) row.get("if_miss");obj.setIf_miss(if_miss);
			ingredients = (String) row.get("ingredients");obj.setIngredients(ingredients);
			packaging = (String) row.get("packaging");obj.setPackaging(packaging);
			packaging_typ = (String) row.get("packaging_typ");obj.setPackaging_typ(packaging_typ);
			mrp = (String) row.get("mrp");obj.setMrp(mrp);
			prescription_required = (String) row.get("prescription_required");obj.setPrescription_required(prescription_required);
			label = (String) row.get("label");obj.setLabel(label);
			Fact_Box = (String) row.get("Fact_Box");obj.setFact_Box(Fact_Box);
			primary_use = (String) row.get("primary_use");obj.setPrimary_use(primary_use);
			storage = (String) row.get("storage");obj.setStorage(storage);
			//common_side_effect = (String) row.get("common_side_effect");obj.setCommon_side_effect(common_side_effect);
			alcoholInteraction = (String) row.get("alcoholInteraction");obj.setAlcoholInteraction(alcoholInteraction);
			pregnancyInteraction = (String) row.get("pregnancyInteraction");obj.setPregnancyInteraction(pregnancyInteraction);
			medicines.add(obj);
		}

		return medicines;
	}

	
	public String bookOrder(OrderModel order) {

		 int patientId = order.getPatientId();
		 
		String purchaseDate = order.getPurchaseDate(); //yyyy-mm-dd
		 String patientContactNo = order.getPatientContactNo();

		 int paymentStatus = order.getPaymentStatus();
			 List<MedicineOrderDetails> medicineOrderList = order.getMedicineList();

		String query1 = "insert into order_txn(patientId,purchaseDate, patientContactNo,paymentStatus) values(123,'2022-08-09 08:30:30', '8826611755',1)";
		String query11 = "insert into order_txn(patientId,purchaseDate, patientContactNo,paymentStatus) values("+ patientId+ ","+"'"+purchaseDate+"'"+",'"+ patientContactNo+"',"+paymentStatus+")";
		String query2 = "insert into order_medicines(orderId,medicineId,medicineName, quantity) values(234,2,'Augmentin 625 Duo Tablet',1)";


		//jdbc.execute(query11);
		
		final String SQL = query11;
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbc.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
			return ps;
		}, keyHolder);

		int orderId = keyHolder.getKey().intValue();

		System.err.print("Key--->" + orderId);
		
		//return "slot created Successfully";

		for (MedicineOrderDetails currentMedicineInOrder : medicineOrderList) {
			 int medicineId = currentMedicineInOrder.getMedicineId();
			 String medicineName = currentMedicineInOrder.getMedicineName();
			 int quantity = currentMedicineInOrder.getQuantity();
				String query22 = "insert into order_medicines(orderId,medicineId,medicineName, quantity) values("+orderId+","+medicineId+",'"+medicineName+"',"+quantity+")";
				jdbc.execute(query22);


		}
		


		return "Successfully Inserted, Txn ID:"+orderId ;
	}

	public OrderModel getOrderDetails(String txnId) {
		OrderModel order = new OrderModel();
		List<MedicineOrderDetails> medicineOrderedList = new ArrayList<MedicineOrderDetails>();

		String sql = "SELECT * FROM order_txn WHERE orderId="+txnId;
		String sql2 = "SELECT * FROM order_medicines WHERE orderId="+txnId;

		List<Map<String, Object>> rows = jdbc.queryForList(sql);
		for (Map row : rows) {
			
		
			int Id = (Integer) row.get("patientId"); 
			order.setPatientId(Id);
			Date date= (Date) row.get("purchaseDate"); 
			order.setPurchaseDate(date.toString());
			String patientContactNo = (String) row.get("patientContactNo"); 
			order.setPatientContactNo(patientContactNo);
			int paymentStatus = (Integer) row.get("paymentStatus"); 
			order.setPatientId(paymentStatus);
			
			int orderId = (Integer) row.get("orderId"); 
			order.setOrderId(orderId);

			List<Map<String, Object>> rows2 = jdbc.queryForList(sql2);
			for (Map row2 : rows2) {
				MedicineOrderDetails medicineOrderDetails = new MedicineOrderDetails();
				int medicineId = (Integer) row2.get("medicineId"); 
				medicineOrderDetails.setMedicineId(medicineId);
				String medicineName = (String) row2.get("medicineName"); 
				medicineOrderDetails.setMedicineName(medicineName);
				int quantity = (Integer) row2.get("quantity"); 
				medicineOrderDetails.setQuantity(quantity);
				
				medicineOrderedList.add(medicineOrderDetails);
				
			}
			
		}
		order.setMedicineList(medicineOrderedList);
		
		return order;
	}

	public List<OrderModel> getOrdersDetails() {

		String sql = "SELECT orderId FROM order_txn";
		List<OrderModel> orderModelList = new ArrayList<OrderModel>();

		List<Map<String, Object>> rows = jdbc.queryForList(sql);
		for (Map row : rows) {

			int txnId = (Integer) row.get("orderId");

			OrderModel order = getOrderDetails(txnId + "");
			
			orderModelList.add(order);
		}

		return orderModelList;

	}

	/**
	 * @param startDateTime1
	 * @param endDateTime1
	 * @param timeZone
	 * @param emailArr
	 * @param requestId
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	@RequestMapping("/createEvent")
	@ResponseBody
	public String createEvent(String startDateTimeStr, String endDateTimeStr, String timeZone, String[] emailArr,
			int requestId) throws IOException, GeneralSecurityException {

		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();

		Event event = new Event().setSummary(SUMMARY).setLocation(LOCATION).setDescription(DESCRIPTION);

		/**
		 * DateTime startDateTime = new DateTime("2015-05-28T09:00:00-07:00");
		 * EventDateTime start = new EventDateTime() .setDateTime(startDateTime)
		 * .setTimeZone("America/Los_Angeles"); event.setStart(start);
		 * 
		 * DateTime endDateTime = new DateTime("2015-05-28T17:00:00-07:00");
		 * EventDateTime end = new EventDateTime() .setDateTime(endDateTime)
		 * .setTimeZone("America/Los_Angeles");
		 */

		DateTime startDateTime = new DateTime(startDateTimeStr);
		EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone(timeZone);
		event.setStart(start);

		DateTime endDateTime = new DateTime(endDateTimeStr);
		EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone(timeZone);
		event.setEnd(end);

		String[] recurrence = new String[] { RECURRENTCE_RULE };
		event.setRecurrence(Arrays.asList(recurrence));

		ArrayList<EventAttendee> eMailList = new ArrayList<EventAttendee>();
		ArrayList<EventAttendee> emailList = new ArrayList<EventAttendee>();
		EventAttendee[] attendeesArr = new EventAttendee[] {};
		for (String emailIdAttendee : emailArr) {
			emailList.add(new EventAttendee().setEmail(emailIdAttendee));
		}
		/**
		 * EventAttendee[] attendees = new EventAttendee[] { new
		 * EventAttendee().setEmail("sbrin@example.com"), new
		 * EventAttendee().setEmail("saurabh.gupta@hcl.com"), new
		 * EventAttendee().setEmail("saurabh2204@yahoo.com"), };
		 */
		event.setAttendees(emailList);

		EventReminder[] reminderOverrides = new EventReminder[] {
				new EventReminder().setMethod("email").setMinutes(24 * 60),
				new EventReminder().setMethod("popup").setMinutes(10), };
		Event.Reminders reminders = new Event.Reminders().setUseDefault(false)
				.setOverrides(Arrays.asList(reminderOverrides));
		event.setReminders(reminders);

		// conferenceData.
		ConferenceData conferenceData = new com.google.api.services.calendar.model.ConferenceData();
		CreateConferenceRequest conferenceRequest = new CreateConferenceRequest();
		ConferenceSolutionKey kk = new ConferenceSolutionKey().setType("hangoutsMeet");
		ConferenceSolution conferenceSolution = new ConferenceSolution();
		conferenceSolution.setKey(kk);

		conferenceRequest.setConferenceSolutionKey(kk);
		conferenceRequest.setRequestId(requestId + "");
		// conferenceRequest.setStatus(new ConferenceRequestStatus().set);
		conferenceRequest.getStatus();
		conferenceData.setCreateRequest(conferenceRequest);

		event.setConferenceData(conferenceData);

		event = service.events().insert(calendarId, event).setConferenceDataVersion(1).execute();

//			service.events().patch(calendarId, event.getId(), event).execute();

		System.out.printf("Event created: %s\n", event.getHtmlLink());

		// addAttachment(service, calendarId, event.getId());

		// service.events().patch(calendarId, eid, event);
		System.err.println("Goole Meet created: %s\n" + event.getHangoutLink());

		return event.getHtmlLink();

	}

	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = SpringBootJdbcController.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		// returns an authorized Credential object.
		return credential;
	}

	/**
	 * @param slot_start
	 * @param slots_end
	 * @throws ParseException
	 */
	public void getSlotDurationDays(String slot_start, String slots_end) throws ParseException {
		slot_start_date_time = StringUtils.split(slot_start, 'T');
		slot_end_date_time = StringUtils.split(slots_end, 'T');

		SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH);
		Date firstDate = sdf.parse(slot_start_date_time[0]);
		Date secondDate = sdf.parse(slot_end_date_time[0]);

		long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
		no_of_days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

		System.err.println(no_of_days);
	}

}