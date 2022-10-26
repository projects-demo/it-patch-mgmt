
package com.example.demo;

import java.awt.print.Book;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

	String filter_slot_end, filter_slot_start, filter_time_zone, filter_region, filter_is_booked;

	@Autowired
	ApplicationContext applicationContext;
	@Autowired
	BookService bookService;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	SpringBootJdbcController springBootJdbcController;

	private String currentServiceId;
	private String currentServiceName;

	private String msg = "";

	/**
	 * Hibernate query Employee.Vehicle
	 * 
	 * @return
	 */
	// @PostMapping(path = "/set-service", consumes = "application/json", produces =
	// "application/json")

	@GetMapping({ "/tmp", "/user_tmp" })
	public String getSlots(Model model) {
		// List<SlotModel> slots = springBootJdbcController.findAllSlots();
		int counter = 1;
		// model.addAttribute("listOfAllSlots", slots);
		return "home.html";
	}

	@GetMapping({ "/", "/user" })
	public String listBooks(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(7);

		String[] filterArr = new String[] { filter_slot_start, filter_slot_end, filter_time_zone, filter_region,
				filter_is_booked };

		Page<SlotModel> bookPage = bookService.findPaginated(PageRequest.of(currentPage - 1, pageSize), filterArr);

		model.addAttribute("bookPage", bookPage);

		int totalPages = bookPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		return "home.html";
	}

	@PostMapping({ "/", "/user" })
	public String listBooksPost(Model model, @RequestParam("filter_slot_start") String filter_slot_start1,
			@RequestParam("filter_slot_end") String filter_slot_end1,
			@RequestParam("time_zone") String filter_time_zone1, @RequestParam("region") String filter_region1,
			@RequestParam(value = "is_booked", required = false) String is_booked) {
		this.filter_slot_end = filter_slot_end1;
		this.filter_slot_start = filter_slot_start1;
		this.filter_time_zone = filter_time_zone1;
		this.filter_region = filter_region1;
		this.filter_is_booked = is_booked;
		if (StringUtils.isNotBlank(filter_is_booked) && StringUtils.equalsIgnoreCase("on", filter_is_booked))
			this.filter_is_booked = "true";

		return listBooks(model, Optional.empty(), Optional.empty());

	}

	/**
	 * @param startDateTime
	 * @param endDateTime
	 * @param emailId
	 * @param requestId
	 * @param timeZone
	 * @param is_booked
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	@RequestMapping(value = "/bookslot-and-create-event", method = RequestMethod.POST)
	public ResponseEntity bookSlotAndCreateEvent(@RequestParam String startDateTime, @RequestParam String endDateTime,
			@RequestParam String emailId, @RequestParam int requestId, @RequestParam String timeZone,
			@RequestParam String is_booked) throws IOException, GeneralSecurityException {
		// System.err.println("sku for adding to estimate----------->" + sku);
		// redirAttrs.addFlashAttribute("message", "This is message from flash");
		// Sample startDateTime-> 2022-08-12 19:30:00.0->2022-08-12T20:30:00.0
		String startDateTimeStr = StringUtils.replace(startDateTime, " ", "T");// + "-00:00";
		String endDateTimeStr = StringUtils.replace(endDateTime, " ", "T");// + "-00:00";

		String[] emailArr = StringUtils.stripAll(StringUtils.split(emailId, ","));
//2022-08-12T20:30:00.0
//2022-08-24T09:00:00.0
		String eventLink = springBootJdbcController.createEvent(startDateTimeStr, endDateTimeStr, timeZone, emailArr,
				requestId);
		System.err.println(emailId);

		springBootJdbcController.updateSlot(requestId, emailId, is_booked, eventLink);
		// return " Success ";
		return new ResponseEntity(HttpStatus.OK);

	}

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String getAdmin() {
		return "admin.html";
	}

	@RequestMapping(value = "/admin1", method = RequestMethod.GET)
	public String getAdmin1() {
		return "admin1.html";
	}

	@RequestMapping(value = "/admin2", method = RequestMethod.GET)
	public String getAdmin2() {
		return "admin2.html";
	}

	@RequestMapping(value = "/admin1", method = RequestMethod.POST)
	public String postAdmin(@ModelAttribute SlotModel slotModel, ModelMap model) throws ParseException {
		// System.err.println(BASE_PATH_CLOUD);
		// utilService.setBASE_PATH_CLOUD(BASE_PATH_CLOUD);
		msg = "";

		springBootJdbcController.insertslot(slotModel.getSlot_start(), slotModel.getSlot_end(),
				slotModel.getIt_email_list(), slotModel.getAttendee_email_list(), slotModel.getRegion(),
				slotModel.isIs_booked(), slotModel.getTime_zone());
		model.addAttribute("msg", "Sucessfully created");

		return "admin1.html";

	}

	@RequestMapping(value = "/admin2", method = RequestMethod.POST)
	public String postAdmin2(@ModelAttribute SlotModel slotModel, ModelMap model)
			throws ParseException, IOException, GeneralSecurityException {
		// System.err.println(BASE_PATH_CLOUD);
		// utilService.setBASE_PATH_CLOUD(BASE_PATH_CLOUD);
		msg = "";
		springBootJdbcController.insertslots(slotModel.getSlot_start(), slotModel.getSlots_end(),
				slotModel.getSlot_duration(), slotModel.getIt_email_list(), slotModel.getAttendee_email_list(),
				slotModel.getRegion(), slotModel.isIs_booked(), slotModel.getTime_zone(),
				slotModel.isInclude_weekends(), slotModel.getHolidays(), slotModel.getCreate_event());

		model.addAttribute("msg", "Sucessfully created");

		return "admin2.html";

	}

	/**
	 * private List<Service> simulateSearchResult(String searchTerm) { //
	 * List<Service> services = json2Java.getGCPServices(); Session session =
	 * sessionFactory.openSession(); session.beginTransaction(); // String hql =
	 * "SELECT serviceId, displayName FROM service WHERE displayName LIKE
	 * '%Bigquery%';"; //SELECT * FROM service WHERE serviceId LIKE '%0885%'; String
	 * hql = "FROM service"; Service service1 = session.get(Service.class,
	 * "0885-57C8-655B"); System.err.println(service1.getDisplayName()); String
	 * likeTagName = "%" + searchTerm + "%"; Criteria crit =
	 * session.createCriteria(Service.class);
	 * crit.add(Restrictions.like("displayName", likeTagName, MatchMode.ANYWHERE));
	 * List<Service> results = crit.list();
	 * 
	 * System.err.println(); List<Service> result = new ArrayList<Service>();
	 * 
	 * for (Service service : results) { if
	 * (StringUtils.containsIgnoreCase(service.getDisplayName(), searchTerm)) {
	 * result.add(service); } }
	 * 
	 * session.getTransaction().commit(); session.close();
	 * 
	 * return result; }
	 */

	public String getCurrentServiceId() {
		return currentServiceId;
	}

	public void setCurrentServiceId(String currentServiceId) {
		this.currentServiceId = currentServiceId;
	}

	public String getCurrentServiceName() {
		return currentServiceName;
	}

	public void setCurrentServiceName(String currentServiceName) {
		this.currentServiceName = currentServiceName;
	}

}
