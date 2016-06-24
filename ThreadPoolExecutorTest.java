package aaa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolExecutorTest {

	private static int mTaskCount = 9;
	private static ExecutorService executorService = Executors.newFixedThreadPool(3);
	private static ExecutorService singleTaskService = Executors.newSingleThreadExecutor();

	public static void main(String[] args) {
		queryTaskTable();  //查询任务表
		comeNewTask();
	}

	/**
	 * 查询任务表中是否有数据
	 */
	private static void queryTaskTable() {
		// postBean
		for (int i = 0; i < 5; i++) {
			singleTaskService.execute(new TaskRunnable(i));
		}
	}

	/**
	 * 来了一个新的任务
	 */
	private static void comeNewTask(){
		try {
			Thread.sleep(2000);
			singleTaskService.execute(new TaskRunnable(8));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 提交任务
	 */
	private static class TaskRunnable implements Runnable {

		private CountDownLatch countDownLatch = new CountDownLatch(9);
		private List<String> newPath = Collections.synchronizedList(new ArrayList<String>());//返回值
		private List<String> faile = Collections.synchronizedList(new ArrayList<String>());//提交失败返回值
		private int no;

		public TaskRunnable(int no) {
			this.no = no; 
		}

		@Override
		public void run() {
			synchronized (ThreadPoolExecutorTest.class) {
				try {
					System.out.println("第" + no + "个任务处理压缩问题");

					// postBean.getPasts; 图片集合
					task(countDownLatch, newPath);
					countDownLatch.await();

//					for (int i = 0; i < newPath.size(); i++) {
//						System.out.println(newPath.get(i));
//					}
					// for paths//
					//requset 
					
					
					System.out.println("提交任务数据");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void task(CountDownLatch countDownLatch, List<String> newPath) {
		for (int i = 0; i < mTaskCount; i++) {  //创建图片处理任务
			// imgpath
			executorService.execute(new CompressRunnable(i, countDownLatch, newPath));
		}
	}

	/**
	 * 压缩任务
	 */
	private static class CompressRunnable implements Runnable {

		private int num;
		private CountDownLatch countDownLatch;
		private List<String> newPath;

		public CompressRunnable(int i, CountDownLatch countDownLatch, List<String> newPath) {
			this.num = i;
			this.countDownLatch = countDownLatch;
			this.newPath = newPath;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(1000);
				SimpleDateFormat dateFormat = new SimpleDateFormat("hh:MM:ss");
				String format = dateFormat.format(new Date());
				System.out.println(num + "    " + format);
				// 压缩 返回的路径
				newPath.add("" + num);
				countDownLatch.countDown();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
