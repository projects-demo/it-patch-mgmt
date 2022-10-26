
package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@Controller
public class HomeController2 {

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

	@PostMapping("/users/login")
	public ResponseEntity loginUser(@Valid @RequestBody UserModel user) {
		String password = springBootJdbcController.findPassword(user.getName());
		if (user.getPassword().equalsIgnoreCase(password))
			return new ResponseEntity(HttpStatus.OK);
		else
			return new ResponseEntity(HttpStatus.UNAUTHORIZED);
	}

	@GetMapping("/medicines/list")
	@CrossOrigin
	@ResponseBody
	public List<MedicineModel> listMedicine() {
		List<MedicineModel> medicineList = springBootJdbcController.getMedicines();

		return medicineList;
	}

	@GetMapping({ "/medicines/find" })
	@CrossOrigin
	@ResponseBody
	public List<MedicineModel> findMedicines(Model model, @RequestParam("medicineName") String medicineName) {
		medicineName = medicineName.trim();
		medicineName = StringUtils.removeEnd(medicineName, "*");
		medicineName = StringUtils.removeStart(medicineName, "*");
		List<MedicineModel> filteredMedicineList = new ArrayList<MedicineModel>();
		for (MedicineModel currentMedicine : listMedicine()) {
			if (currentMedicine.getName().contains(medicineName)) {
				filteredMedicineList.add(currentMedicine);
			}

		}
		return filteredMedicineList;

	}

	@PostMapping("/order/book")
	@ResponseBody
	@CrossOrigin
	public String bookOrder(@Valid @RequestBody OrderModel order) {
		String txnId = springBootJdbcController.bookOrder(order);
		return txnId;
	}

	@GetMapping("/order/get-order")
	@ResponseBody
	@CrossOrigin
	public OrderModel getOrderDetails(Model model, @RequestParam("txnId") String txnId) {
		OrderModel orderModel = springBootJdbcController.getOrderDetails(txnId);
		return orderModel;
	}

	@GetMapping("/order/get-orders")
	@ResponseBody
	@CrossOrigin
	public List<OrderModel> getOrdersDetails(Model model) {
		List<OrderModel> orderModelList = springBootJdbcController.getOrdersDetails();
		return orderModelList;
	}

	
}
