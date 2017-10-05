package simulator;

import java.util.Arrays;
import java.util.LinkedList;

public class Table {

	public final int MAX_TPAL = 8;
	public final int MAX_TBL = 64;
	public final int MAX_TC = 8;

	private int[][] mc;
	private int[] lrufif = {0, 0, 0, 0, 0, 0, 0, 0};
	private boolean estado = false;
	private int tpal;
	private int tbl;
	private int tc;

	public int aciertos = 0;
	public int intentos = 0;

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

	public void colocaBloq(int dir, int operacion) {
		intentos++; //Se incrementa los intentos por cada dirección que se mete.
		int bp = calculaBloqPrin(calculaPal(dir));
		int cj = calculaConj(bp, 8/tc);

		if(8/tc == 1) {
			for(int i = 0; i < mc.length; i++) {
				if(mc[i][4] == bp) {
					
					if(operacion == 1) { //Notifica que ha modificado.
						mc[cj][1] = 1;
					}
					
					aciertos++; //Se incrementan los aciertos para la tasa de aciertos por cada dirección.

					estado = true; //El estado de la operación se cambia si hay acierto o fallo.

					if(politicaRemplazo == PoliticaRemplazo.LRU) { //Si se utiliza LRU.
						lrufif[i] = 0;
					} else {
						lrufif[i]++;
					}
					
					break;
				} else {
					if(i == 7) {
						estado = false;
						int maxj = 0; //Indice del más mayor.
						for(int j = 0; j < mc.length; j++) {
							if(lrufif[maxj] < lrufif[j]) {
								maxj = j;
							}
						}
						
						mc[maxj][0] = 1;
						mc[maxj][1] = 0;
						mc[maxj][2] = bp;
						mc[maxj][4] = bp; //Se traslada el bloque.
						lrufif[maxj] = 0;

					}
				}
			}
		} else if(8/tc == 2) {

			if(cj == 0) {
				for(int i = 0; i < 4; i++) {
					if(mc[i][4] == bp) {
						
						if(operacion == 1) { //Notifica que ha modificado.
							mc[cj][1] = 1;
						}
						
						aciertos++; //Se incrementan los aciertos para la tasa de aciertos por cada dirección.

						estado = true; //El estado de la operación se cambia si hay acierto o fallo.

						if(politicaRemplazo == PoliticaRemplazo.LRU) { //Si se utiliza LRU.
							lrufif[i] = 0;
						} else {
							lrufif[i]++;
						}
						
						break;
					} else {
						if(i == 3) {
							estado = false;
							int maxj = 0; //Indice del más mayor.
							for(int j = 0; j < 4; j++) {
								if(lrufif[maxj] < lrufif[j]) {
									maxj = j;
								}
							}
							
							mc[maxj][0] = 1;
							mc[maxj][1] = 0;
							mc[maxj][2] = bp/2;
							mc[maxj][4] = bp; //Se traslada el bloque.
							lrufif[maxj] = 0;

						}
					}
				}
			} else {
				for(int i = 4; i < mc.length; i++) {
					if(mc[i][4] == bp) {
						
						if(operacion == 1) { //Notifica que ha modificado.
							mc[cj][1] = 1;
						}
						
						aciertos++; //Se incrementan los aciertos para la tasa de aciertos por cada dirección.

						estado = true; //El estado de la operación se cambia si hay acierto o fallo.

						if(politicaRemplazo == PoliticaRemplazo.LRU) { //Si se utiliza LRU.
							lrufif[i] = 0;
						} else {
							lrufif[i]++;
						}
						
						break;
					} else {
						if(i == 7) {
							estado = false;
							int maxj = 0; //Indice del más mayor.
							for(int j = 4; j < mc.length; j++) {
								if(lrufif[maxj] < lrufif[j]) {
									maxj = j;
								}
							}
							
							
							mc[maxj][0] = 1;
							mc[maxj][1] = 0;
							mc[maxj][2] = bp/2;
							mc[maxj][4] = bp; //Se traslada el bloque.
							lrufif[maxj] = 0;

						}
					}
				}
			}



			// De cara a más funcionalidades.
			//				for(int j = 7; i >= 0; j--) {
			//
			//				}

		} else if(8/tc == 4) {
			if(cj == 0) {
				for(int i = 0; i < 2; i++) {
					
					if(mc[i][4] == bp) {
						
						if(operacion == 1) { //Notifica que ha modificado.
							mc[cj][1] = 1;
						}
						
						aciertos++; //Se incrementan los aciertos para la tasa de aciertos por cada dirección.

						estado = true; //El estado de la operación se cambia si hay acierto o fallo.

						if(politicaRemplazo == PoliticaRemplazo.LRU) { //Si se utiliza LRU.
							lrufif[i] = 0;
						} else {
							lrufif[i]++;
						}
						
						break;
					} else {
						if(i == 1) {
							estado = false;
							int maxj = 0; //Indice del más mayor.
							for(int j = 0; j < 2; j++) {
								if(lrufif[maxj] < lrufif[j]) {
									maxj = j;
								}
							}
							
							mc[maxj][0] = 1;
							mc[maxj][1] = 0;
							mc[maxj][2] = bp/4;
							mc[maxj][4] = bp; //Se traslada el bloque.
							lrufif[maxj] = 0;

						}
					}
				}
			} else if(cj == 1) {
				for(int i = 2; i < 4; i++) {
					
					if(mc[i][4] == bp) {
						
						if(operacion == 1) { //Notifica que ha modificado.
							mc[cj][1] = 1;
						}
						
						aciertos++; //Se incrementan los aciertos para la tasa de aciertos por cada dirección.

						estado = true; //El estado de la operación se cambia si hay acierto o fallo.

						if(politicaRemplazo == PoliticaRemplazo.LRU) { //Si se utiliza LRU.
							lrufif[i] = 0;
						} else {
							lrufif[i]++;
						}
						
						break;
					} else {
						if(i == 3) {
							estado = false;
							int maxj = 2; //Indice del más mayor.
							for(int j = 2; j < 4; j++) {
								if(lrufif[maxj] < lrufif[j]) {
									maxj = j;
								}
							}
							
							mc[maxj][0] = 1;
							mc[maxj][1] = 0;
							mc[maxj][2] = bp/4;
							mc[maxj][4] = bp; //Se traslada el bloque.
							lrufif[maxj] = 0;

						}
					}
				}
			} else if(cj == 2) {
				for(int i = 4; i < 6; i++) {
					
					if(mc[i][4] == bp) {
						
						if(operacion == 1) { //Notifica que ha modificado.
							mc[cj][1] = 1;
						}
						
						aciertos++; //Se incrementan los aciertos para la tasa de aciertos por cada dirección.

						estado = true; //El estado de la operación se cambia si hay acierto o fallo.

						if(politicaRemplazo == PoliticaRemplazo.LRU) { //Si se utiliza LRU.
							lrufif[i] = 0;
						} else {
							lrufif[i]++;
						}
						
						break;
					} else {
						if(i == 5) {
							estado = false;
							int maxj = 4; //Indice del más mayor.
							for(int j = 4; j < 6; j++) {
								if(lrufif[maxj] < lrufif[j]) {
									maxj = j;
								}
							}
							
							mc[maxj][0] = 1;
							mc[maxj][1] = 0;
							mc[maxj][2] = bp/4;
							mc[maxj][4] = bp; //Se traslada el bloque.
							lrufif[maxj] = 0;

						}
					}
				}
			} else if(cj == 3) {
				for(int i = 6; i < mc.length; i++) {
					if(mc[i][4] == bp) {
						
						if(operacion == 1) { //Notifica que ha modificado.
							mc[cj][1] = 1;
						}
						
						aciertos++; //Se incrementan los aciertos para la tasa de aciertos por cada dirección.

						estado = true; //El estado de la operación se cambia si hay acierto o fallo.

						if(politicaRemplazo == PoliticaRemplazo.LRU) { //Si se utiliza LRU.
							lrufif[i] = 0;
						} else {
							lrufif[i]++;
						}
						
						break;
					} else {
						if(i == 7) {
							estado = false;
							int maxj = 6; //Indice del más mayor.
							for(int j = 6; j < mc.length; j++) {
								if(lrufif[maxj] < lrufif[j]) {
									maxj = j;
								}
							}
							
							
							mc[maxj][0] = 1;
							mc[maxj][1] = 0;
							mc[maxj][2] = bp/4;
							mc[maxj][4] = bp; //Se traslada el bloque.
							
							lrufif[maxj] = 0;

						}
					}
				}
			}
		} else if(8/tc == 8) {
			if(mc[cj][4] == bp) {
				
				if(operacion == 1) {
					mc[cj][1] = 1;
				}
				
				aciertos++;
				estado = true;
			} else {
				estado = false;
				mc[cj][0] = 1;
				mc[cj][1] = 0;
				mc[cj][2] = bp/8;
				mc[cj][4] = bp;
			}
		}
	}

	public void calculaTiempoTot() {
		float h = (float) aciertos/intentos;
		System.out.println("Referencias: " + intentos + " -- Aciertos: " + aciertos + " -- Tasa de aciertos, h = " + h);
		System.out.println("Tiempo total = " + h*2+(1-h)*(2+(21+((tbl/tpal)-1))*2)+(1-h)*(2+21+((tbl/tpal)-1)));
		System.exit(0);
	}

	//	ocup mod tag rem || bloque
	//	 -----------------------------------
	//	 0 0 0 0 || ---
	//	 0 0 0 0 || ---
	//	 --------------------------------
	//	 0 0 0 0 || ---
	//	 0 0 0 0 || ---
	//	 --------------------------------
	//	 0 0 0 0 || ---
	//	 0 0 0 0 || ---
	//	 --------------------------------
	//	 1 1 8 0 || b35
	//	 0 0 0 0 || ---
	//	 --------------------------------

}
