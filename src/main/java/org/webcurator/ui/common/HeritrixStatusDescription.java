package org.webcurator.ui.common;

import java.util.HashMap;
import java.util.Map;

public class HeritrixStatusDescription {
	
	private static final HashMap<Integer, String> description = new HashMap<Integer, String>();
	
	public HeritrixStatusDescription() {
		description.put(1, "Successful DNS lookup");
		description.put(0, "Fetch never tried (perhaps protocol unsupported or illegal URI)");
		description.put(-1, "DNS lookup failed");
		description.put(-2, "HTTP connect failed");
		description.put(-3, "HTTP connect broken");
		description.put(-4, "HTTP timeout (before any meaningful response received)");
		description.put(-5, "Unexpected runtime exception; see runtime-errors.log");
		description.put(-6, "Prerequisite domain-lookup failed, precluding fetch attempt");
		description.put(-7, "URI recognized as unsupported or illegal");
		description.put(-8, "Multiple retries all failed, retry limit reached");
		description.put(-50, "Temporary status assigned URIs awaiting preconditions; appearance in logs may be a bug");
		description.put(-60, "Failure status assigned URIs which could not be queued by the Frontier (and may in fact be unfetchable)");
		description.put(-61, "Prerequisite robots.txt-fetch failed, precluding a fetch attempt");
		description.put(-62, "Some other prerequisite failed, precluding a fetch attempt");
		description.put(-63, "A prerequisite (of any type) could not be scheduled, precluding a fetch attempt");
		description.put(-3000, "Severe Java 'Error' conditions (OutOfMemoryError, StackOverflowError, etc.) during URI processing.");
		description.put(-4000, "'chaff' detection of traps/content of negligible value applied");
		description.put(-4001, "Too many link hops away from seed");
		description.put(-4002, "Too many embed/transitive hops away from last URI in scope");
		description.put(-5000, "Out of scope upon reexamination (only happens if scope changes during crawl)");
		description.put(-5001, "Blocked from fetch by user setting");
		description.put(-5002, "Blocked by a custom processor");
		description.put(-5003, "Blocked due to exceeding an established quota");
		description.put(-5004, "Blocked due to exceeding an established runtime");
		description.put(-6000, "Deleted from Frontier by user");
		description.put(-7000, "Processing thread was killed by the operator (perhaps because of a hung condition)");
		description.put(-9998, "Robots.txt rules precluded fetch");
		
		// we also include the HTTP status codes that can be returned
		description.put(100, "Continue");
		description.put(101, "Switching protocols");
		description.put(200, "Successful");
		description.put(201, "Created");
		description.put(202, "Accepted");
		description.put(203, "Non-authoritative information");
		description.put(204, "No content");
		description.put(205, "Reset content");
		description.put(206, "Partial content");
		description.put(300, "Multiple choices");
		description.put(301, "Moved permanently");
		description.put(302, "Moved temporarily");
		description.put(303, "See other location");
		description.put(304, "Not modified");
		description.put(305, "Use proxy");
		description.put(307, "Temporary redirect");
		description.put(400, "Bad request");
		description.put(401, "Not authorized");
		description.put(403, "Forbidden");
		description.put(404, "Not found");
		description.put(405, "Method not allowed");
		description.put(406, "Not acceptable");
		description.put(407, "Proxy authentication required");
		description.put(408, "Request timeout");
		description.put(409, "Conflict");
		description.put(410, "Gone");
		description.put(411, "Length required");
		description.put(412, "Precondition failed");
		description.put(413, "Request entity too large");
		description.put(414, "Requested URI is too long");
		description.put(415, "Unsupported media type");
		description.put(416, "Requested range not satisfiable");
		description.put(417, "Expectation failed");
		description.put(500, "Internal server error");
		description.put(501, "Not implemented");
		description.put(502, "Bad gateway");
		description.put(503, "Service unavailable");
		description.put(504, "Gateway timeout");
		description.put(505, "HTTP version not supported");
	}
	
	public final static String getDescription(Integer statusCode) {
		HeritrixStatusDescription instance = new HeritrixStatusDescription();
		return instance.description.get(statusCode);
	}
	
	public final static Map<Integer, String> getMap() {
		HeritrixStatusDescription instance = new HeritrixStatusDescription();
		return instance.description;
	}
}
