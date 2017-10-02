package simulator;

import java.util.Arrays;

public class Table {
	
	public final int MAX_TPAL = 8;
	public final int MAX_TBL = 64;
	public final int MAX_TC = 8;
	
	private int[][] mc;
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
		
		this.mc = new int[MAX_TC][5];
		
		//Inicializar a -1 la ultima columna de cada fila
		int lastIndex = mc[0].length - 1;
		for (int[] i : mc) {
			i[lastIndex] = -1;
		}
		
		//System.out.println(Arrays.deepToString(mc)); //Print array content
		
	}

	public PoliticaRemplazo getPoliticaRemplazo() {
		return politicaRemplazo;
	}

	public void setPoliticaReemplazo(PoliticaRemplazo pr) {
		this.politicaRemplazo = pr;
	}
	
	public void imprimeTabla(int[][] mc, int tamConj) {
		
		int bloq = 0;
		
		StringBuilder sb = new StringBuilder();
		sb.append("ocup\tmod\ttag\trem\t||\tbloque\n");
		sb.append("----------------------------------------------------\n");
		
		int size = mc[0].length - 1;
		for (int i = 0; i < mc.length; i++) {
			for (int j = 0; j < mc[i].length; j++) {
				
				if(j < size) { 						//Si NO es la ultima columna
					sb.append(mc[i][j]+"\t");
				} else {							//Si ES la ultima columna
					sb.append("||\t");
					if(mc[i][size] != -1) { 	//Si NO es -1 (NO esta vacio)
						sb.append("b" + mc[i][size]);
					} else {					//Si ES -1 (ESTA vacio)
						sb.append("---");
					}
				}
				
			}
			
			bloq++;
			
			if(bloq == tamConj) {
				sb.append("\n----------------------------------------------------");
				bloq = 0;
			}
			
			sb.append("\n");
			
		}
		
		System.out.println(sb.toString());
		
	}
	
	public int calculaPal(int dir) { //Se le pasa la dirección byte para obtener la palabra.
		return dir/tpal;
	}
	
	public int calculaBloqprin(int pal) {
		return pal/(tbl/tpal); //Nos dará el tamaño de bloques en palabras con lo que podremos descubrir el bloque de la MP.
	}
	
	public int calculaConj(int bp, int numconj) {
		return bp%numconj;
	}
	
	public int calculaTag(int bp, int numconj) {
		return bp/numconj;
	}

	public void imprimirResultado(int dir, int numconj) {
		int bp = 0, pal = 0, tag = 0, conj = 0;
		
		pal = calculaPal(dir);
		bp = calculaBloqprin(pal);
		conj = calculaConj(bp, numconj);
		tag = calculaTag(bp, numconj);
		
		System.out.println(">Direccion: " + dir + " - Palabra: " + pal + " - Bloque: " + bp);
		System.out.println(">Conjunto: " + conj + " - Tag: " + tag);
	}
	
}
