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

@WebFilter("/profile")
public class ProfileFilter extends HttpFilter {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpSession session =req.getSession();
		Member member =(Member)session.getAttribute("member");
		
		if ( member!= null ) {
			chain.doFilter(req, resp);
			
		}else {
			
			resp.setStatus(401)	;	    
			return;
		}
	}
	
	

}
