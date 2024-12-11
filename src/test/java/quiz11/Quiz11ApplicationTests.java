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
		// ���P���j��g�k
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
		// lambda foreach ( �Ѽ� ) -> { ��@ }
		list.forEach((item) -> {
			System.out.println(item);
		});
		// �ܼƥu���@�ӮɡA�p�A���i�H�ٲ�
		list.forEach(item -> {
			System.out.println(item);
		});
		// ��@���e�u���@��ɡA�j�A���i�H�ٲ��A�����]�n����
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

	// �ΦW���O
	// �����ѬO�ΨӴ����Ӥ���"�u��"�w�q�@�Ӥ�k�A�ҥH�����������w�q��2�Ӥ�k�ɡA�|����
	// ���ε���I�s���q (lambda ��F��) �ӭ��s�w�q����������k�ɡA�~�o�{�L�k�ϥ�
	@FunctionalInterface
	private interface Ifc {
		void run(String name);
	};

	@Test
	public void ifTest() {
		// �إߤ���(�άO��H��k)���骺�ΦW���O
		Ifc ifs1 = new Ifc() {

			@Override
			public void run(String name) {
				System.out.println(name + "runing");
			}

		};
		ifs1.run("�p��");
		System.out.println("==================");
		// ------------------------
		// ���s�w�q�Ҧ���k�A�ϥ� lambda ��F�� (�������u���w�q""�@��""��k���ɫJ�ϥ�)
		// �]���w�q�b�������� run ��k�S���ѼơA�ҥH�U��p�A�����]���ݭn���Ѽ�
		Ifc ifs2 = (name) -> {
			// ���s�w�q Ifc ��k�᪺��@���e
			System.out.println(name + "runing");
		};
		ifs2.run("�p��");
		System.out.println("==================");

	}

	@Test
	public void filterTest() {
		List<Integer> list = new ArrayList<>();
		for (int i = 1; i <= 20; i++) {
			list.add(i);
		}
		// �䰸��
		// ��k�@ filter �� �S���ϥΤj�A��
		List<Integer> evenList1 = list.stream().filter((item) -> item % 2 == 0).collect(Collectors.toList());

		// ��k�G filter �� �ϥΤj�A�� return ����
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
		// funtion<String, Integer> ���� String �P Integer
		// String(T) : �����O���s�w�q Apply ��k���ѼƸ�ƫ��A
		// Integer(R) : �����O���s�w�q Apply ��k���浲�G���^�ǭȸ�ƫ��A
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
