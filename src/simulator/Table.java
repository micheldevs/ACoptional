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

	/**
	 * Imprime por consola la informacion de la memoria cache y haciendo una separacion entre cada distinto conjunto de bloques
	 * 
	 * @param mc Array con la informacion de la cache
	 * @param tamConj El numero de bloques por conjunto
	 */
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

	public int getTamPal() {
		return tpal;
	}

	public void setTamPal(int tamPal) {
		this.tpal = tamPal;
	}

	public int getTamBloq() {
		return tbl;
	}

	public void setTamBloq(int tamBloq) {
		this.tbl = tamBloq;
	}

	public int getTamCache() {
		return tc;
	}

	public void setTamCache(int tamCache) {
		this.tc = tamCache;
	}

	public int calculaPal(int dir) { //Se le pasa la dirección byte para obtener la palabra.
		return dir/tpal;
	}

	public int calculaBloqPrin(int pal) {
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
		bp = calculaBloqPrin(pal);
		conj = calculaConj(bp, numconj);
		tag = calculaTag(bp, numconj);

		System.out.println(">Direccion: " + dir + " - Palabra: " + pal + " - Bloque: " + bp);
		System.out.println(">Conjunto: " + conj + " - Tag: " + tag);
	}

	public void imprimirEstado(boolean estado, int operacion, int dirty, int tambloqenpals) { 
		/*El estado determinará fallo o acierto, operación si lectura 0 o escritura 1, dirty si sucio 1 o limpio 0 y tambloqenpals,
		el tamaño de bloque en palabras.*/
		StringBuilder sb = new StringBuilder();
		//>Tiempo de acceso: busqueda cache, 2 -- transferir bloque (M>C o C>M), 21+7
		//>T_acc: 30 ciclos
		sb.append(">Tiempo de acceso: busqueda cache, 2");
		int tacc = 2;
		
		if(!estado) {
			if(operacion == 0) {
				if(dirty == 0) {
					sb.append("-- transferir bloque (M>C o C>M), 21+" + (tambloqenpals-1)*1);
					tacc = 2+21+(tambloqenpals-1)*1;
				} else if(dirty == 1) {
					sb.append("-- transferir bloque (M>C o C>M) con reemplazo y dirty, 21+21" + (tambloqenpals-1)*1 + "+" + (tambloqenpals-1)*1);
					tacc = 2+21+21+(tambloqenpals-1)*1+(tambloqenpals-1)*1;
				}
			} else if(operacion == 1) {
				if(dirty == 0) {
					sb.append("-- transferir bloque (M>C o C>M), 21+" + (tambloqenpals-1)*1);
					tacc = 2+21+(tambloqenpals-1)*1;
				} else if(dirty == 1) {
					sb.append("-- transferir bloque (M>C o C>M) con reemplazo y dirty, 21+21" + (tambloqenpals-1)*1 + "+" + (tambloqenpals-1)*1);
					tacc = 2+21+21+(tambloqenpals-1)*1+(tambloqenpals-1)*1;
				}
			}
		}
		
		sb.append("/n");
		sb.append(">T_acc: " + tacc + " ciclos");
	}

}
