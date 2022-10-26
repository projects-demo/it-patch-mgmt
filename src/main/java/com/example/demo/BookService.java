package com.example.demo;

import java.awt.print.Book;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookService {
	@Autowired
	SpringBootJdbcController springBootJdbcController;

	public Page<SlotModel> findPaginated(Pageable pageable, String[] filterArr) {
		// List<Book> books = BookUtils.buildBooks();
		List<SlotModel> books = springBootJdbcController.findAllSlots(filterArr);

		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<SlotModel> list;

		if (books.size() < startItem) {
			list = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, books.size());
			list = books.subList(startItem, toIndex);
		}

		Page<SlotModel> bookPage = new PageImpl<SlotModel>(list, PageRequest.of(currentPage, pageSize), books.size());

		return bookPage;

	}
}