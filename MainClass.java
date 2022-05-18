package com.wipro.bt.mainpackage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wipro.bt.beanpackage.InputDO;
import com.wipro.bt.beanpackage.OutputDO;

public class MainClass {

	private static final Object START = "Start";
	private static final Object END = "End";

	public static void main(String[] args) {
		try {
			 if (args.length > 0) {
				 System.out.println(args[0]);
			 byte[] bytes = Files.readAllBytes(Paths.get(args[0]));
				/*
				 * byte[] bytes = Files .readAllBytes(Paths.
				 * get("G:\\Pavithra\\New folder\\Java_TestCase\\sessionSummary_1.txt"));
				 */
			String fileContent = new String(bytes);
			String[] inputArray = fileContent.split(System.lineSeparator());

			List<InputDO> dataList = createDataObjectList(inputArray);

			// Getting First log Time and Last log Time
			InputDO firstLogTimeDO = dataList.stream()
					.collect(Collectors.minBy(Comparator.comparing(InputDO::getTimeVal))).get();
			LocalTime firstLogTime = firstLogTimeDO.getTimeVal();
			InputDO lastLogTimeDO = dataList.stream()
					.collect(Collectors.maxBy(Comparator.comparing(InputDO::getTimeVal))).get();
			LocalTime lastLogTime = lastLogTimeDO.getTimeVal();

			// Breaking session based on users
			Map<String, List<InputDO>> groupByUser = dataList.stream()
					.collect(Collectors.groupingBy(InputDO::getUserName));

			// Get session timing, count for users
			List<OutputDO> outputList = new ArrayList<>();
			groupByUser.forEach((user, list) -> {
				OutputDO outputDO = compute(user, list, firstLogTime, lastLogTime);
				outputList.add(outputDO);
			});
			for (OutputDO data : outputList) {
				System.out.println(data.getUserName() + " " + data.getSessionCount() + " " + data.getSessionTime());
			}
			
			 } else { System.out.println("No Input Parameters..."); }
			
		} catch (Exception e) {
			System.out.println("Exception occured at MainClass -> main Method..." + e.toString());
		}
	}

	private static OutputDO compute(String user, List<InputDO> list, LocalTime firstLogTime, LocalTime lastLogTime) {
		try {
			list.sort((InputDO ido1, InputDO ido2) -> ido1.getTimeVal().compareTo(ido2.getTimeVal()));
			Map<String, List<InputDO>> activityList = list.stream()
					.collect(Collectors.groupingBy(InputDO::getActivity));
			int sessionCount = 0;
			Long sessionTime = 0L;
			List<InputDO> startList = activityList.get(START);
			List<InputDO> endList = activityList.get(END);
			for (InputDO inputDO : startList) {
				for (InputDO inputDO2 : endList) {
					if (inputDO2.getFlag().equals(Boolean.TRUE))
						if (inputDO.getTimeVal().isBefore(inputDO2.getTimeVal())) {
							sessionCount = sessionCount + 1;
							sessionTime = sessionTime
									+ (inputDO.getTimeVal().until(inputDO2.getTimeVal(), ChronoUnit.SECONDS));
							inputDO.setFlag(Boolean.FALSE);
							inputDO2.setFlag(Boolean.FALSE);
							break;
						}
				}
			}
			for (InputDO inputDO : startList) {
				if (inputDO.getFlag().equals(Boolean.TRUE)) {
					sessionCount = sessionCount + 1;
					sessionTime = sessionTime + (inputDO.getTimeVal().until(lastLogTime, ChronoUnit.SECONDS));
				}
			}
			for (InputDO inputDO : endList) {
				if (inputDO.getFlag().equals(Boolean.TRUE)) {
					sessionCount = sessionCount + 1;
					sessionTime = sessionTime + (firstLogTime.until(inputDO.getTimeVal(), ChronoUnit.SECONDS));
				}
			}
			OutputDO outputDO = new OutputDO(user, sessionCount, sessionTime);
			return outputDO;
		} catch (Exception e) {
			System.out.println("Exception occured at MainClass -> compute Method..." + e.toString());
		}
		return null;
	}

	private static List<InputDO> createDataObjectList(String[] inputArray) {
		try {
			List<InputDO> dataList = new ArrayList<>();
			for (int iter = 0; iter < inputArray.length; iter++) {
				String iteration = inputArray[iter];
				String[] lineItem = iteration.split(" ");
				InputDO data = new InputDO(lineItem[0], lineItem[1], lineItem[2]);
				dataList.add(data);
			}
			return dataList;
		} catch (Exception e) {
			System.out.println("Exception occured at MainClass -> createDataObjectList Method..." + e.toString());
		}
		return null;
	}

}
