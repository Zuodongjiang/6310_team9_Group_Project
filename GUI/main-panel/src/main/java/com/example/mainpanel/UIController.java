package com.example.mainpanel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UIController {
	@GetMapping(value = "/")
	public String index() {
		return "index";
    }
}