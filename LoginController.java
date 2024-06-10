package com.example.Innovationproject.controller;

import java.io.BufferedReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.Innovationproject.InnovationMainProjectApplication;
import com.example.Innovationproject.dao.Dao;
import com.example.Innovationproject.entity.UserData;
import com.example.Innovationproject.otp.EmailSenderservice;



@Controller
public class LoginController {
	@Autowired
	Dao dao;
	
	@Autowired
	private EmailSenderservice emailSenderservice;

	@RequestMapping("/loginpage")
	public String loginpage() {
		return "login.html";
	}
	
	@RequestMapping("/signpage")
	public String signpage() {
		return "signpage.html";
	}
	
	@RequestMapping("/savedata")
	public String savedata(UserData userData,Model model){
		UserData ud=dao.findByUsername(userData.getUsername());
		if(ud==null) {
		dao.save(userData);
		return "login";
		}
		else {
			model.addAttribute("msg","Invlaid user name");
			return "signpage.html";
		}
		
	}
	String mailString="";
	String usernameString="";
	@RequestMapping("/login")
	public ModelAndView login(@RequestParam("emailid") String emailid,@RequestParam("password") String password) {
		ModelAndView mv;
		mailString=emailid;
		UserData userdata=dao.findByEmailid(emailid);
		 usernameString=userdata.getUsername();
		if(userdata!=null && userdata.getPassword().equals(password)) {
			mv=new ModelAndView("otp.html");
			return mv;
		}
		else {
			mv=new ModelAndView("login.html");
			mv.addObject("msg","Invalid Cradentials..!! ReTry 😎");
			return mv;
		}
		
	}
	
	String otString="";
	@RequestMapping("/otp")
	public void otp(@RequestParam("emailid") String emailid,Model model) {
		
		if(emailid.equals(mailString)) {
		String otp=emailSenderservice.generateOtp();
		otString=otp;
		String body=otp+" this is once time password of innovation resturent /n"
				+ "don't share any one";
		String subjcetString="Innovation resturent OTP Check";
		String result=emailSenderservice.sendSimpleEmail(emailid,otp,body,subjcetString);
			if ("successfully".equals(result)) {
                model.addAttribute("otpmsgs", "Sent successfully");
            } else {
                model.addAttribute("otpmsg", "Failed to send email");
            }
			mailString="";
			
		}
		else {
			model.addAttribute("otpmsg","Email id not a correct");
			mailString="";
		}
	}
	@RequestMapping("/check")
	public ModelAndView check(@RequestParam("otp") String otp) {
		ModelAndView mv;
		if (otp.equals(otString)) {
			mv=new ModelAndView("homepage.html");
			otString="";
			mv.addObject("msg",usernameString);
			usernameString="";
			return mv;
		}
		else {
			mv=new ModelAndView("otp.html");
			mv.addObject("msg","Invalid OTP..!! ReTry 😎");
			otString="";
			return mv;
		}
		
		
	}
	

}
