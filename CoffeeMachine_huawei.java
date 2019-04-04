public class MinTime {

	public static void main(String[] args) throws IOException{
		int count, n, m, x, y;
		int[] t;
		Scanner sc = new Scanner(System.in);
		count = sc.nextInt();
		MinTime mt = new HelloWorld();
		Coffee s ;
		for(int i = 0; i<count; i++) {
			n = sc.nextInt();
			m = sc.nextInt();
			x = sc.nextInt();
			y = sc.nextInt();
			t = new int[m];
			for(int j= 0; j<m; j++)
				t[j] = sc.nextInt();
			s = mt.new Coffee(n, m, x, y, t);
			System.out.println(s.totalTime());
		}
	}
		
		
	
	class Coffee{
		int n, m, x, y, wStatus;
		int[] cTime, cStatus, bTime, eTime;
		
		Coffee(int n, int m, int x, int y, int[] t){
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
		
		void choose1(int i){
			int minCoffee = 0;
			int min = cTime[0] * (cStatus[0] + 1);//如果我选0号咖啡机，直到煮咖啡结束的时间花费
			for(int j=1; j<m; j++) {
				if(cTime[j] * (cStatus[j] + 1) < min) {
					min = cTime[j] * (cStatus[j] + 1);
					minCoffee = j;
				}
				
			}
			bTime[i] = cTime[minCoffee] * cStatus[minCoffee];
			eTime[i] = bTime[i] + cTime[minCoffee];
			cStatus[minCoffee]++;
		}
		
		void choose2(int i) {
			int wait = wStatus*x + eTime[0] - eTime[i] < 0 ? 0: wStatus*x + eTime[0] - eTime[i]; //从0开始计数，目前需要等待的时间
			
			if(wait+x<y) {//如果等待时间加上洗杯子时间比自己挥发块
				wStatus++;
				eTime[i]+=x;
			}
			else
				eTime[i]+=y;
		}
		
		int totalTime() {
			for(int i=0; i<n; i++) {
				choose1(i);
			}
			for(int i=0; i<n; i++) {
				choose2(i);
			}
			//Arrays.sort(eTime);
			return eTime[n-1];
		}
	}
	
}
