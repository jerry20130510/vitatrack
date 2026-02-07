package core.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import web.member.vo.Member;

//@WebFilter("/*")
public class LoginFilter extends HttpFilter {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		Member member = (Member)req.getSession().getAttribute("member");
		
		if ( member!= null ) {
			chain.doFilter(req, resp);
		}else {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
			return;
		}
	}
}
