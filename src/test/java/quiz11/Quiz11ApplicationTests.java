package quiz11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Quiz11ApplicationTests {

	@Test
	void lambdaTest() {
		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(2);
		list.add(3);
		// 不同的迴圈寫法
		// for loop
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
		System.out.println("==================");
		// foreach
		for (Integer item : list) {
			System.out.println(item);
		}
		System.out.println("==================");
		// lambda foreach ( 參數 ) -> { 實作 }
		list.forEach((item) -> {
			System.out.println(item);
		});
		// 變數只有一個時，小括號可以省略
		list.forEach(item -> {
			System.out.println(item);
		});
		// 實作內容只有一行時，大括號可以省略，分號也要拿掉
		list.forEach(item -> System.out.println(item));
		// =============== Map for loop ====================
		Map<Integer, String> map = new HashMap<>();
		map.put(1, "A");
		map.put(2, "B");
		map.put(3, "C");
		System.out.println("==================");
		// foreach -- entrySet
		for (Entry<Integer, String> item : map.entrySet()) {
			System.out.println("key: " + item.getKey());
			System.out.println("value: " + item.getValue());
		}
		System.out.println("==================");
		// foreach --keySet
		for (Integer item : map.keySet()) {
			System.out.println("key: " + item);
			System.out.println("value: " + map.get(item));
		}
		System.out.println("==================");
		// foreach --lambda
		map.forEach((k, v) -> {
			System.out.println("key: " + k);
			System.out.println("value: " + v);
		});
	}

	// 匿名類別
	// 此註解是用來提醒該介面"只能"定義一個方法，所以此介面中有定義第2個方法時，會報錯
	// 不用等到呼叫階段 (lambda 表達式) 來重新定義介面中的方法時，才發現無法使用
	@FunctionalInterface
	private interface Ifc {
		void run(String name);
	};

	@Test
	public void ifTest() {
		// 建立介面(或是抽象方法)實體的匿名類別
		Ifc ifs1 = new Ifc() {

			@Override
			public void run(String name) {
				System.out.println(name + "runing");
			}

		};
		ifs1.run("小明");
		System.out.println("==================");
		// ------------------------
		// 重新定義所有方法，使用 lambda 表達示 (介面中只有定義""一個""方法的時侯使用)
		// 因為定義在介面中的 run 方法沒有參數，所以下方小括號中也不需要有參數
		Ifc ifs2 = (name) -> {
			// 重新定義 Ifc 方法後的實作內容
			System.out.println(name + "runing");
		};
		ifs2.run("小王");
		System.out.println("==================");

	}

	@Test
	public void filterTest() {
		List<Integer> list = new ArrayList<>();
		for (int i = 1; i <= 20; i++) {
			list.add(i);
		}
		// 找偶數
		// 方法一 filter 中 沒有使用大括號
		List<Integer> evenList1 = list.stream().filter((item) -> item % 2 == 0).collect(Collectors.toList());

		// 方法二 filter 中 使用大括號 return 分號
		List<Integer> evenList2 = list.stream().filter((item) -> {
			return (item % 2) == 0;
		})//
				.collect(Collectors.toList());

	}

	@FunctionalInterface
	interface Funtion<T, R> {
		R apply(T t);
	}
	
	@Test
	public void funtionTest() {
		// funtion<T, R>
		// funtion<String, Integer> 中的 String 與 Integer
		// String(T) : 指的是重新定義 Apply 方法中參數資料型態
		// Integer(R) : 指的是重新定義 Apply 方法執行結果的回傳值資料型態
		Funtion<String, Integer> fun = new Funtion<>() {

			@Override
			public Integer apply(String t) {
				return Integer.parseInt( t );
			}

		};
		fun.apply("1");
	}
	
	@Test
	public void peedicateTest( ) {
		Predicate<Integer> pre = new Predicate<>() {

			@Override
			public boolean test(Integer t) {
				return t%2 == 0;
			}
			
		};
		System.out.println(pre.test(2));
	}

}
