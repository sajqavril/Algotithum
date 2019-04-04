/*
题目描述：有n个人，m台咖啡机，每台咖啡机有自己的制作时间，一次只能做一杯
每个人喝咖啡的时间不计，喝完之后要么选择洗杯器要么自己挥发干，时间分别为x和y
只有一台洗杯器，要排队等待
求最少时间--
*/

/*
思路：每一套mnxy的配置抽象成为一个Coffee对象，计算该对象所对应的最短时间
以人为视角切入，每个人面临两个选择：
	1. 选择哪一台咖啡机：得到该人等待咖啡机的时间和煮咖啡的时间
	2. 选择洗杯器还是自己挥发干：得到该人等待洗杯器的时间和洗杯子的时间（如果不等待洗杯器就不用等待洗杯器时间）
选择的确定：
	1. 从时间0开始，到选择了一台咖啡机并且时间最短的那台咖啡机，具体如下
		1.1 init 假设初始选择第0台咖啡机, minTime = wait(0) + cook(0), min = 0
		1.2 for 1 to m-1 咖啡机：
			if 某一台咖啡机所用的时间+等待时间 cook(i) + wait(i)<目前所选的咖啡机 cook(min) + wait(min)
				min = i, minTime = wait(i) + cook(i);
			update 该人等待咖啡机的时间以及煮完咖啡的时间
	2. 从时间最短的咖啡机工作完之后，洗碗机才进入自己的时间线，所以洗碗机开始时间记为最短咖啡机的工作时间begin=min{cook(i)}，具体如下：
		2.1 init 假设初始等待时间洗碗机wait:
			if begin+洗碗机工作人数*洗碗机工作时间<该人煮完咖啡的时间，说明该人在煮完咖啡之前，洗碗机就空闲了不用等待
				wait=0
			else 还要等待洗碗机 
				wait=begin+洗碗机工作人数*洗碗机工作时间-该人煮完咖啡的时间
		2.2 选择等待还是自己挥发
			if wait+x<y 等待洗碗机
				endTime += wait + x, 洗碗机工作人数++
			else 等待挥发
				endTime += y
每个人做完选择之后，最短时间就是最大的endTime，结束。

重点！！！！
个人认为此题的核心就是时间线的记录，当时完全没有想清楚怎么记录时间线，然后晚上回去继续想了一下就大概确定了这个思路，根据相对等待时间来决定绝对时间
另一点是咖啡机和洗杯机两者的时间怎么重合起来，重点要想清楚洗杯机的时间要从第一个人完成咖啡制作之后（第一个人一定选择最短工作时间的咖啡机）才生效，等待时间的比较也是一个点
如果都是顺序遍历每一个人的话，最后的时间不用排序，最后一个人一定是最后一个完成的，因此只用拿最后一个人的endTime即可

最开始看到这个问题的时候，会想到os的资源抢占，排队等待之类，但个人实在不知道如何用多线程来实现，于是想了这样一个静态的办法
				
*/

public class MinTime {

	public static void main(String[] args) throws IOException{
		int count, n, m, x, y;
		int[] t;
		Scanner sc = new Scanner(System.in);
		count = sc.nextInt();
		MinTime mt = new HelloWorld(); 
		//因为此时调用main方法的时候，main是静态方法，随着类型的加载初始化，但是如果需要使用内部类Coffee对象，需要用外部类的对象
		Coffee s ;//内部类对象
		for(int i = 0; i<count; i++) {
			n = sc.nextInt();
			m = sc.nextInt();
			x = sc.nextInt();
			y = sc.nextInt();
			t = new int[m];
			for(int j= 0; j<m; j++)
				t[j] = sc.nextInt();
			s = mt.new Coffee(n, m, x, y, t); //用外部类对象初始化内部类对象
			System.out.println(s.totalTime()); 
		}
	}
		
		
	
	class Coffee{
		int n, m, x, y, wStatus; //wStatus对应目前使用了洗杯机的总人数
		int[] cTime, cStatus, bTime, eTime; //分别是每个咖啡机煮咖啡的时间，每个咖啡机的使用人数，每个人的开始时间（鸡肋），每个人的结束时间
		
		Coffee(int n, int m, int x, int y, int[] t){ //初始化所有变量
			this.n = n;
			this.m = m;
			this.x = x;
			this.y = y;
			cTime = new int[m];
			for(int k=0; k<m; k++)
				cTime[k] = t[k];
			cStatus = new int[m];
			bTime = new int[n];
			eTime = new int[n];
			wStatus = 0;
		}
		
		void choose1(int i){ //选择咖啡机
			int minCoffee = 0; //假设0号咖啡机耗时最短
			int min = cTime[0] * (cStatus[0] + 1);//如果我选0号咖啡机，直到煮咖啡结束的时间花费
			for(int j=1; j<m; j++) {
				if(cTime[j] * (cStatus[j] + 1) < min) { //更新最短时间咖啡机选择（等待时间+使用时间）
					min = cTime[j] * (cStatus[j] + 1);
					minCoffee = j;
				}
				
			}
			bTime[i] = cTime[minCoffee] * cStatus[minCoffee]; //开始时间
			eTime[i] = bTime[i] + cTime[minCoffee]; //结束时间
			cStatus[minCoffee]++; //使用咖啡机人数++
		}
		
		void choose2(int i) {
			//从0开始计数，目前需要等待的时间
			int wait = wStatus*x + eTime[0] - eTime[i] < 0 ? 0: wStatus*x + eTime[0] - eTime[i];
			
			if(wait+x<y) {//如果等待时间加上洗杯子时间比自己挥发块
				wStatus++; //洗杯机使用人数++
				eTime[i]+=x;
			}
			else
				eTime[i]+=y;
		}
		
		int totalTime() { //最短时间计算
			for(int i=0; i<n; i++) {
				choose1(i);
			}
			for(int i=0; i<n; i++) {
				choose2(i);
			}
			//Arrays.sort(eTime); 最后那个人一定是最后完成的
			//其实上面的choose的执行可以每个人先后执行choose1 choose2
			return eTime[n-1];
		}
	}
	
}
