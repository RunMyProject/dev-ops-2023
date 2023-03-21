package myspring.checkmime.config;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import io.jsonwebtoken.ExpiredJwtException;
import myspring.checkmime.service.CheckmimeUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class HttpLogoutHandler implements LogoutSuccessHandler {

	@Value("${jwt.auth}")
	private String auth;

	@Autowired
	private CheckmimeUserService jwtInMemoryUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	public HttpLogoutHandler(JwtTokenUtil jwtUtil) {
		this.jwtTokenUtil = jwtUtil;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
								Authentication authentication) throws IOException {
		// Remove token in redis.
		final String requestTokenHeader = request.getHeader(auth);

		String username = null;
		String jwtToken = null;
		// JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
			}
		} else {
			System.out.println("JWT Token does not begin with Bearer String");
		}

		if(username!=null) {

			final CheckmimeUserDetails checkmimeUserDetails = jwtInMemoryUserDetailsService.loadUserByUsername(username);
			jwtInMemoryUserDetailsService.logoutUser(checkmimeUserDetails);

			jwtTokenUtil.deleteToken((UserDetails) checkmimeUserDetails);

			// System.out.println("LOGOUT");
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().flush();
	}
}