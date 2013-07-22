package com.denimgroup.threadfix.service.framework;

import java.util.ArrayList;
import java.util.List;

import com.denimgroup.threadfix.data.entities.DataFlowElement;
import com.denimgroup.threadfix.data.entities.Finding;
import com.denimgroup.threadfix.data.entities.Scan;

public class CommonPathFinder {
	
	private CommonPathFinder(){}

	public static final String findOrParseProjectRoot(Scan scan) {
		return parseRoot(getFilePaths(scan));
	}

	public static final String findOrParseUrlPath(Scan scan) {
		return parseRoot(getUrlPaths(scan));
	}

	private static List<String> getFilePaths(Scan scan) {
		if (scan == null || scan.getFindings() == null
				|| scan.getFindings().isEmpty()) {
			return null;
		}

		List<String> returnString = new ArrayList<>();

		for (Finding finding : scan.getFindings()) {
			if (finding.getIsStatic()) {
				List<DataFlowElement> dataFlowElements = finding.getDataFlowElements();
				if (dataFlowElements == null || dataFlowElements.size() == 0)
					continue;

				if (dataFlowElements.get(0) != null
						&& dataFlowElements.get(0).getSourceFileName() != null) {
					returnString.add(dataFlowElements.get(0)
							.getSourceFileName());
				}
			}
		}

		return returnString;
	}

	private static List<String> getUrlPaths(Scan scan) {
		if (scan == null || scan.getFindings() == null
				|| scan.getFindings().isEmpty()) {
			return null;
		}

		List<String> returnStrings = new ArrayList<>();

		for (Finding finding : scan.getFindings()) {
			if (finding != null && finding.getSurfaceLocation() != null
					&& finding.getSurfaceLocation().getPath() != null) {
				returnStrings.add(finding.getSurfaceLocation().getPath());
			}
		}

		return returnStrings;
	}

	private static String parseRoot(List<String> items) {
		if (items == null || items.isEmpty())
			return null;

		String commonPrefix = null;

		for (String string : items) {
			if (commonPrefix == null) {
				commonPrefix = string;
			} else {
				commonPrefix = findCommonPrefix(string, commonPrefix);
			}
		}

		if (commonPrefix != null && !commonPrefix.equals("")) {
			if (commonPrefix.contains("/")) {
				while (commonPrefix.endsWith("/")) {
					commonPrefix = commonPrefix.substring(0,
							commonPrefix.length() - 1);
				}

				if (commonPrefix.contains("/")) {
					commonPrefix = commonPrefix.substring(
							commonPrefix.lastIndexOf("/") + 1).replace("/", "");
				}
			}
		}

		return commonPrefix;
	}

	private static String findCommonPrefix(String newString, String oldString) {
		if (newString == null || oldString == null)
			return "";
		if (newString.toLowerCase().contains(oldString.toLowerCase()))
			return oldString;

		String newLower = newString.replace("\\", "/").toLowerCase();
		String oldLower = oldString.replace("\\", "/").toLowerCase();

		String returnString = "";

		for (String string : oldLower.split("/")) {
			String tempString = returnString.concat(string + "/");
			if (newLower.startsWith(tempString)) {
				returnString = tempString;
			} else {
				break;
			}
		}

		return oldString.replace("\\", "/").substring(0, returnString.length());
	}
	
}
