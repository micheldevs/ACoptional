package simulator;

public class Table {
	
	public final int MAX_TPAL = 8;
	public final int MAX_TBL = 64;
	public final int MAX_TC = 8;
	
	public static int[][] mc = {{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};
	public static String[] bloqc = {"---", "---", "---", "---", "---", "---", "---", "---"};
	private int tpal;
	private int tbl;
	private int tc;
	
	private PoliticaRemplazo politicaRemplazo;
	
	/**
	 * Politica de reemplazo de la memoria cache.
	 */
	public enum PoliticaRemplazo {
	    FIFO, LRU
	}
	
	public Table(int tpal, int tbl, int tc) {
		tpal = this.tpal;
		tbl = this.tbl;
		tc = this.tc;
	}

	public PoliticaRemplazo getPoliticaRemplazo() {
		return politicaRemplazo;
	}

	public void setPoliticaReemplazo(PoliticaRemplazo pr) {
		this.politicaRemplazo = pr;
	}
	
	public void imprimirTabla() {
		System.out.println("  ocup  mod  tag  rem || bloque");
		System.out.println("---------------------------------");
		
		for(int i = 0; i < mc.length; i++) {
			for(int j = 0; j < mc[i].length; j++) {
				System.out.print(mc[i][j] + " ");	
			}
				System.out.print(" || " + bloqc[i]);
				
				if(i < tc) {
					System.out.println("---------------------------------");
				}
				
				//Falta completar pero va por buen camino.

		}
	}
	
	
}
