<%@taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>
<%@ page import="org.acegisecurity.ui.AbstractProcessingFilter"%>
<%@ page
	import="org.acegisecurity.ui.webapp.AuthenticationProcessingFilter"%>
<%@ page import="org.acegisecurity.AuthenticationException"%>
<%@ page import="org.acegisecurity.context.SecurityContextHolder"%>
<%@ page import="org.acegisecurity.userdetails.UserDetails"%>
<%@ page import="gov.nih.nci.system.web.util.JSPUtils"%>
<%
			String lastUserKey = (String) session
			.getAttribute(AuthenticationProcessingFilter.ACEGI_SECURITY_LAST_USERNAME_KEY);
	if (lastUserKey == null || lastUserKey.equalsIgnoreCase("null")) {
		lastUserKey = "";
	}
	//out.println("lastUserKey: " + lastUserKey);

	String loginErrorStr = request.getParameter("login_error");
	boolean isLoginError = false;
	if (loginErrorStr != null && loginErrorStr.length() > 0) {
		isLoginError = true;
	}
	//out.println("isLoginError: " + isLoginError);
	JSPUtils jspUtils= JSPUtils.getJSPUtils(config.getServletContext());
	boolean isSecurityEnabled = jspUtils.isSecurityEnabled();

	boolean isAuthenticated = false;
	String userName = "";
	if (isSecurityEnabled){
		Object obj = SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
	
		//out.println("obj: " + obj);
		if (obj instanceof UserDetails) {
			userName = ((UserDetails) obj).getUsername();
		} else {
			userName = obj.toString();
		}
	
		if (userName != null
				&& !(userName.equalsIgnoreCase("anonymousUser"))) {
			isAuthenticated = true;
		}
	}
	//out.println("userName: " + userName);
	boolean webinterfaceDisabled=jspUtils.isWebInterfaceDisabled();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
		<title><s:text name="home.title" />
		</title>
		<link rel="stylesheet" type="text/css" href="styleSheet.css" />
		<script src="script.js" type="text/javascript"></script>
	</head>
	<body>
		<table summary="" cellpadding="0" cellspacing="0" border="0"
			width="100%" height="100%">

			<%@ include file="include/header.inc"%>

			<tr>
				<td height="100%" align="center" valign="top">
					<table summary="" cellpadding="0" cellspacing="0" border="0"
						height="100%" width="771">

						<%@ include file="include/applicationHeader.inc"%>

						<tr>
							<td valign="top">
								<table summary="" cellpadding="0" cellspacing="0" border="0"
									height="100%" width="100%">
									<tr>
										<td height="20" class="mainMenu">

											<%@ include file="include/mainMenu.inc"%>

										</td>
									</tr>

									<!--_____ main content begins _____-->
									<tr>
										<td valign="top">
											<!-- target of anchor to skip menus -->
											<a name="content" />
												<table summary="" cellpadding="0" cellspacing="0" border="0"
													class="contentPage" width="100%" height="100%">

													<!-- banner begins -->
													<tr>
														<td class="bannerHome">
															<img src="images/c_ca_core_big.gif">

														</td>
													</tr>
													<!-- banner begins -->

													<tr>
														<td height="100%">

															<!-- target of anchor to skip menus -->
															<a name="content" />

																<table summary="" cellpadding="0" cellspacing="0"
																	border="0" height="100%">
																	<tr>
																		<td width="70%">

																			<!-- welcome begins -->
																			<table summary="" cellpadding="0" cellspacing="0"
																				border="0" height="100%">
																				<tr>
																					<td class="welcomeTitle" height="20">
																						WELCOME TO THE OBJECT CART
																					</td>
																				</tr>
																				<tr>
																					<td class="welcomeContent" valign="top">
																						The Object Cart application is an application built on top of the caCORE SDK with the purpose of providing shopping cart type operations for data objects.  
																					    The Object Cart provides a client API that client applications can utilize to create instance of object "carts" to be able to store, retrieve, update, and delete data objects.  
																						Both Java objects adhering to the JavaBean specification and XML documents representing data objects can be passed to the client API.  
													
																						The initial users of the Object Cart application are the CDE Browser and Forms Builder applications, allowing users to share their collections of objects 
																						(Administered Components, CDEs, etc...) amongst the two applications while keeping the CDE Browser and Forms Builder decoupled.
																						<br><br>
																						
																						References:
																						<br>

																						<ul>
																							<li>
																								<a
																									href="http://gforge.nci.nih.gov/projects/mdr/" target="_blank">MDR GForge site</a> - Contains news, information,
																								documents, defects, feedback, and reports on ObjectCart.																							</li>
																							<li>
																								<a
																									href="http://ncicb.nci.nih.gov/download/objectcartapilicenseagreement.jsp" target="_blank">ObjectCart Download site</a> - Contains documents,
																								information, and downloads for the ObjectCart application.
																							</li>
																							<li>
																								<a
																									href="https://wiki.nci.nih.gov/display/caDSR/Object+Cart+API+1.0.2+Release+Notes" target="_blank">ObjectCart 1.0.2 Release Notes</a> - Release Notes for all
																								caCORE products for version 1.0
																							</li>
																							<li>
																								<a href="docs/index.html">ObjectCart 1.0.2
																									javadocs</a>
																							</li>
																						</ul>
																					</td>
																				</tr>
																			</table>
																			<!-- welcome ends -->

																		</td>
																		<td valign="top" width="30%">

																			<!-- sidebar begins -->
																			<table summary="" cellpadding="0" cellspacing="0"
																				border="0" height="100%">

																				<!-- login/continue form begins -->
																				<tr>
																					<td valign="top">
																						<%
																						if (webinterfaceDisabled) {
																						%>
																						<table summary="" cellpadding="2" cellspacing="0"
																							border="0" width="100%" class="sidebarSection">
																							<tr>
																								<td class="sidebarTitle" height="20">
																									SELECT CRITERIA
																								</td>
																							</tr>
																							<tr>
																								<td class="sidebarContent" align="center">
																									WebInterface is Disabled
																								</td>
																							</tr>
																						</table>
																						<%
																						} else if (isSecurityEnabled && !isAuthenticated) {
																						%>

																						<table summary="" cellpadding="2" cellspacing="0"
																							border="0" width="100%" class="sidebarSection">
																							<tr>
																								<td class="sidebarTitle" height="20">
																									<s:text name="home.login" />
																								</td>
																							</tr>
																							<tr>
																								<td class="sidebarContent">
																									<s:form method="post"
																										action="j_acegi_security_check"
																										name="loginForm" theme="css_xhtml">
																										<table cellpadding="2" cellspacing="0"
																											border="0">
																											<%
																											if (isLoginError) {
																											%>
																											<tr>
																												<td class="sidebarLogin" align="left"
																													colspan="2">
																													<font color="red"> Your login
																														attempt was not successful; please try
																														again.<BR> <BR> Reason: <%=((AuthenticationException) session
												.getAttribute(AbstractProcessingFilter.ACEGI_SECURITY_LAST_EXCEPTION_KEY))
												.getMessage()%> <BR> <BR> </font>
																												</td>
																											</tr>
																											<%
																											}
																											%>

																											<tr>
																												<td class="sidebarLogin" align="left">
																													<s:text name="home.loginID" />
																												</td>
																												<td class="formFieldLogin">
																													<s:textfield name="j_username"
																														value="%{lastUserKey}"
																														cssClass="formField" size="14" />
																												</td>
																											</tr>
																											<tr>
																												<td class="sidebarLogin" align="left">
																													<s:text name="home.password" />
																												</td>
																												<td class="formFieldLogin">
																													<s:password name="j_password"
																														cssClass="formField" size="14" />
																												</td>
																											</tr>
																											<tr>
																												<td>
																													&nbsp;
																												</td>
																												<td>
																													<s:submit cssClass="actionButton"
																														type="submit" value="Login" />
																												</td>
																											</tr>
																										</table>
																									</s:form>
																								</td>
																							</tr>
																						</table>
																						<%
																						} else {
																						%>

																						<table summary="" cellpadding="2" cellspacing="0"
																							border="0" width="100%" class="sidebarSection">
																							<tr>
																								<td class="sidebarTitle" height="20">
																									SELECT CRITERIA
																								</td>
																							</tr>
																							<tr>
																								<td class="sidebarContent" align="center">
																									<s:form method="post"
																										action="ShowDynamicTree.action"
																										name="continueForm" theme="simple">
																										<s:submit cssClass="actionButton"
																											value="Continue" theme="simple" />
																									</s:form>
																								</td>
																							</tr>
																						</table>
																						<%
																						}
																						%>
																					</td>
																				</tr>
																				<!-- login ends -->

																				<!-- what's new begins -->
																				<tr>
																					<td valign="top">
																						<table summary="" cellpadding="0" cellspacing="0"
																							border="0" width="100%" class="sidebarSection">
																							<tr>
																								<td class="sidebarTitle" height="20">
																									WHAT'S NEW
																								</td>
																							</tr>
																							<tr>
																								<td class="sidebarContent">
																									<ul>
																										<li>
																											508 Compliance
																										</li>

																										<li>
																											New User Interface
																										</li>
																										<li>
																											New hierarchical package/class browsing
																										</li>
																										<li>
																											New single-session authentication
																										</li>
																									</ul>
																								</td>
																							</tr>
																						</table>
																					</td>
																				</tr>
																				<!-- what's new ends -->

																				<!-- did you know? begins -->
																				<tr>
																					<td valign="top">
																						<table summary="" cellpadding="0" cellspacing="0"
																							border="0" width="100%" height="100%"
																							class="sidebarSection">
																							<tr>
																								<td class="sidebarTitle" height="20">
																									DID YOU KNOW?
																								</td>
																							</tr>
																							<tr>
																								<td class="sidebarContent" valign="top">
																									&nbsp;
																								</td>
																							</tr>
																						</table>
																					</td>
																				</tr>
																				<!-- did you know? ends -->

																				<!-- spacer cell begins (keep for dynamic expanding) -->
																				<tr>
																					<td valign="top" height="100%">
																						<table summary="" cellpadding="0" cellspacing="0"
																							border="0" width="100%" height="100%"
																							class="sidebarSection">
																							<tr>
																								<td class="sidebarContent" valign="top">
																									&nbsp;
																								</td>
																							</tr>
																						</table>
																					</td>
																				</tr>
																				<!-- spacer cell ends -->

																			</table>
																			<!-- sidebar ends -->

																		</td>
																	</tr>
																</table>
														</td>
													</tr>
												</table>
										</td>
									</tr>
									<!--_____ main content ends _____-->

									<tr>
										<td height="20" class="footerMenu">

											<!-- application ftr begins -->
											<%@ include file="include/applicationFooter.inc"%>
											<!-- application ftr ends -->

										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>

					<%@ include file="include/footer.inc"%>

				</td>
			</tr>
		</table>
	</body>
</html>