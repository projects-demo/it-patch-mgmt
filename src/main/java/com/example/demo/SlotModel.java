package com.example.demo;

public class SlotModel {

	public String slot_start;
	public String slot_end;
	public String slots_end;
	public String slot_duration;
	public String time_zone;
	public String it_email_list;
	public String attendee_email_list;
	public String region;
	public boolean is_booked;
	public boolean include_weekends;
	public String holidays;
	public String create_event;

	public String getCreate_event() {
		return create_event;
	}

	public void setCreate_event(String create_event) {
		this.create_event = create_event;
	}

	public String getHolidays() {
		return holidays;
	}

	public void setHolidays(String holidays) {
		this.holidays = holidays;
	}

	public boolean isInclude_weekends() {
		return include_weekends;
	}

	public void setInclude_weekends(boolean include_weekends) {
		this.include_weekends = include_weekends;
	}

	public String getSlot_start() {
		return slot_start;
	}

	public void setSlot_start(String slot_start) {
		this.slot_start = slot_start;
	}

	public String getSlot_end() {
		return slot_end;
	}

	public void setSlot_end(String slot_end) {
		this.slot_end = slot_end;
	}

	public String getTime_zone() {
		return time_zone;
	}

	public void setTime_zone(String time_zone) {
		this.time_zone = time_zone;
	}

	public String getIt_email_list() {
		return it_email_list;
	}

	public void setIt_email_list(String it_email_list) {
		this.it_email_list = it_email_list;
	}

	public String getAttendee_email_list() {
		return attendee_email_list;
	}

	public void setAttendee_email_list(String attendee_email_list) {
		this.attendee_email_list = attendee_email_list;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public boolean isIs_booked() {
		return is_booked;
	}

	public void setIs_booked(Boolean boolean1) {
		this.is_booked = boolean1;
	}

	public long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setIs_booked(boolean is_booked) {
		this.is_booked = is_booked;
	}

	public String getSlot_duration() {
		return slot_duration;
	}

	public void setSlot_duration(String slot_duration) {
		this.slot_duration = slot_duration;
	}

	public String getSlots_end() {
		return slots_end;
	}

	public void setSlots_end(String slots_end) {
		this.slots_end = slots_end;
	}


}