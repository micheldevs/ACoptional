package simulator;

import java.util.LinkedList;

public class Table {

	public final int MAX_TPAL = 8;
	public final int MAX_TBL = 64;
	public final int MAX_TC = 8;

	private int tiempoMemCache = 2; //Ciclos necesarios para el acceso a la memoria cache	
	private int tiempoMemPrincipal = 21; //Ciclos necesarios para el acceso a la memoria principal
	private int tiempoBuffer = 1; // Ciclos necesarios para el buffer
	private int tiempoBloque = -1; // Ciclos necesarios para transferir el bloque

	private int totalCiclos = 0;

	private int[][] mc;
	private int[] lrufif = {0, 0, 0, 0, 0, 0, 0, 0};

	private LinkedList<Integer> listaBloques = new LinkedList<Integer>();

	private int tamPal;
	private int tamBloq;
	private int tamCache;

	private boolean acierto = false;
	private boolean dirty = false;
	
	private int numAciertos;
	private int numReferencias;

	private PoliticaRemplazo politicaRemplazo;

	/**
	 * Politica de reemplazo de la memoria cache.
	 */
	public enum PoliticaRemplazo {
		FIFO, LRU
	}

	/**
	 * Tipo de operacion.<br>
	 * <b>LD</b> - lectura<br>
	 * <b>ST</b> - escritura
	 */
	public enum TipoOperacion {
		LD(0),
		ST(1);	

		private final int op;

		private TipoOperacion(int op) {
			this.op = op;
		}

		/**
		 * Optener el valor del {@code enum}
		 * 
		 * @return el valor
		 */
		public int getValor() {
			return op;
		}

	}

	public Table(int tpal, int tbl, int tc) {
		this.tamPal = tpal;
		this.tamBloq = tbl;
		this.tamCache = tc;

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
	 * Obtener los ciclos necesarios para acceder a la memoria cache
	 * 
	 * @return los ciclos que se tardan
	 */
	public int getTiempoMemCache() {
		return tiempoMemCache;
	}

	/**
	 * Establecer los ciclos necesarios para acceder a la memoria cache
	 *  
	 * @param tiempoMemCache ciclos que se tardan
	 */
	public void setTiempoMemCache(int tiempoMemCache) {
		this.tiempoMemCache = tiempoMemCache;
	}

	/**
	 * Obtener los ciclos necesarios para acceder a la memoria principal
	 * 
	 * @return los ciclos que se tardan
	 */
	public int getTiempoMemPrincipal() {
		return tiempoMemPrincipal;
	}

	/**
	 * Establecer los ciclos necesarios para acceder a la memoria principal
	 *  
	 * @param tiempoMemPrincipal ciclos que se tardan
	 */
	public void setTiempoMemPrincipal(int tiempoMemPrincipal) {
		this.tiempoMemPrincipal = tiempoMemPrincipal;
	}

	/**
	 * Obtener los ciclos necesarios para el buffer
	 * 
	 * @return los ciclos que se tardan
	 */
	public int getTiempoBuffer() {
		return tiempoBuffer;
	}

	/**
	 * @param tiempoBuffer the tiempoBuffer to set
	 */
	public void setTiempoBuffer(int tiempoBuffer) {
		this.tiempoBuffer = tiempoBuffer;
	}

	/**
	 * Establecer los ciclos necesarios para la transferencia del bloque
	 *  
	 * @param tiempoBuffer ciclos que se tardan
	 */
	public void setTiempoBloque(int tiempoBloque) {
		this.tiempoBloque = tiempoBloque;
	}

	/**
	 * Obtener los ciclos necesarios para la transferencia del bloque
	 * 
	 * @return los ciclos que se tardan
	 */
	public int getTiempoBloque() {
		if (tiempoBloque < 0) {
			// Tbl = Tmp + (tam.bloque - 1) * Tbuff
			tiempoBloque = tiempoMemPrincipal + (getPalabrasDentroBloque() - 1) * tiempoBuffer; 
		}
		return tiempoBloque;
	}

	public int[][] getMc() {
		return mc;
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
		return tamPal;
	}

	public void setTamPal(int tamPal) {
		this.tamPal = tamPal;
	}

	public int getTamBloq() {
		return tamBloq;
	}

	public void setTamBloq(int tamBloq) {
		this.tamBloq = tamBloq;
	}

	public int getTamCache() {
		return tamCache;
	}

	public void setTamCache(int tamCache) {
		this.tamCache = tamCache;
	}

	public int calculaPal(int dir) { //Se le pasa la dirección byte para obtener la palabra.
		return dir/tamPal;
	}

	/**
	 * Calcula el numero de palabras que corresponden por bloque
	 * 
	 * @return numero de palabras por bloque
	 */
	public int getPalabrasDentroBloque() {
		return tamBloq/tamPal;
	}

	public int calculaBloqPrin(int pal) {
		return pal/(getPalabrasDentroBloque()); // Nos dará el tamaño de bloques en palabras con lo que podremos descubrir el bloque de la MP.
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

		System.out.println("> Direccion: " + dir + " - Palabra: " + pal + " - Bloque: " + bp);
		System.out.println("> Conjunto: " + conj + " - Tag: " + tag);

		if(acierto) {
			System.out.println("> ACIERTO EN LA CACHE");
		} else {
			System.out.println("> FALLO EN LA CACHE");
		}

	}

	//>Tiempo de acceso: busqueda cache, 2 -- transferir bloque (M>C o C>M), 21+7
	//>T_acc: 30 ciclos
	public void imprimirEstado(int bloqCache) {

		/*
		 * if ( acierto) {
		 * 		T = Tmc
		 * } else if (bit dirty) {
		 * 		T = Tmc + Tbl + Tbl
		 * } else {
		 * 		//fallo sin bit dirty
		 * 		T = Tmc + Tbl
		 * }
		 * 
		 */

		StringBuilder sb = new StringBuilder();

		if(acierto) {
			sb.append("> Tiempo de acceso: busqueda cache, " + getTiempoMemCache());			
		} else {
			sb.append("-- transferir bloque (M>C o C>M)");
			int tempVal = (getPalabrasDentroBloque() - 1) * tiempoBuffer;
			if(!isDirty(bloqCache)) {
				sb.append(", " + getTiempoMemPrincipal() + " + " + tempVal);
			} else {
				sb.append("con reemplazo y dirty, " + getTiempoMemPrincipal() + " + " + tempVal + " + " + getTiempoMemPrincipal() + " + " + tempVal);	
			}
		}
		sb.append("\n> T_acc: " + getCiclos(acierto, dirty) + " ciclos");

		System.out.println(sb.toString());
	}

	public void colocaBloq(int dir, TipoOperacion operacion) {
		numReferencias++; //Se incrementa los intentos por cada dirección que se mete.
		int bp = calculaBloqPrin(calculaPal(dir));
		int cj = calculaConj(bp, 8/tamCache);
		
		dirty = mc[cj][1] == 1;

		if(8/tamCache == 1) {
			for(int i = 0; i < mc.length; i++) {
				if(mc[i][4] == bp) {


					if(operacion == TipoOperacion.ST) { //Notifica que ha modificado.
						mc[i][1] = 1;
					}

					numAciertos++; //Se incrementan los aciertos para la tasa de aciertos por cada dirección.

					acierto = true; //El estado de la operación se cambia si hay acierto o fallo.

					if(politicaRemplazo == PoliticaRemplazo.LRU) { //Si se utiliza LRU.
						lrufif[i] = 0;
					} else {
						lrufif[i]++;
					}

					break;
				} else {
					if(i == 7) {
						acierto = false;

						int maxj = 0; //Indice del más mayor.
						for(int j = 0; j < mc.length; j++) {
							if(mc[j][0] == 0) {
								maxj = j;
								break;
							} else if(lrufif[maxj] < lrufif[j]) {
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
		} else if(8/tamCache == 2) {

			if(cj == 0) {
				for(int i = 0; i < 4; i++) {
					if(mc[i][4] == bp) {

						if(operacion == TipoOperacion.ST) { //Notifica que ha modificado.
							mc[i][1] = 1;
						}

						numAciertos++; //Se incrementan los aciertos para la tasa de aciertos por cada dirección.

						acierto = true; //El estado de la operación se cambia si hay acierto o fallo.

						if(politicaRemplazo == PoliticaRemplazo.LRU) { //Si se utiliza LRU.
							lrufif[i] = 0;
						} else {
							lrufif[i]++;
						}

						break;
					} else {
						if(i == 3) {
							acierto = false;
							int maxj = 0; //Indice del más mayor.
							for(int j = 0; j < 4; j++) {
								if(mc[j][0] == 0) {
									maxj = j;
									break;
								} else if(lrufif[maxj] < lrufif[j]) {
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

					lrufif[i]++;
				}
			} else {
				for(int i = 4; i < mc.length; i++) {
					if(mc[i][4] == bp) {

						if(operacion == TipoOperacion.ST) { //Notifica que ha modificado.
							mc[i][1] = 1;
						}


						numAciertos++; //Se incrementan los aciertos para la tasa de aciertos por cada dirección.

						acierto = true; //El estado de la operación se cambia si hay acierto o fallo.

						if(politicaRemplazo == PoliticaRemplazo.LRU) { //Si se utiliza LRU.
							lrufif[i] = 0;
						} else {
							lrufif[i]++;
						}

						break;
					} else {
						if(i == 7) {
							acierto = false;
							int maxj = 0; //Indice del más mayor.
							for(int j = 4; j < mc.length; j++) {
								if(mc[j][0] == 0) {
									maxj = j;
									break;
								} else if(lrufif[maxj] < lrufif[j]) {
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

					lrufif[i]++;
				}
			}



			// De cara a más funcionalidades.
			//				for(int j = 7; i >= 0; j--) {
			//
			//				}

		} else if(8/tamCache == 4) {
			if(cj == 0) {
				for(int i = 0; i < 2; i++) {

					if(mc[i][4] == bp) {

						if(operacion == TipoOperacion.ST) { //Notifica que ha modificado.
							mc[i][1] = 1;
						}

						numAciertos++; //Se incrementan los aciertos para la tasa de aciertos por cada dirección.

						acierto = true; //El estado de la operación se cambia si hay acierto o fallo.

						if(politicaRemplazo == PoliticaRemplazo.LRU) { //Si se utiliza LRU.
							lrufif[i] = 0;
						} else {
							lrufif[i]++;
						}

						break;
					} else {
						if(i == 1) {
							acierto = false;
							int maxj = 0; //Indice del más mayor.
							for(int j = 0; j < 2; j++) {
								if(mc[j][0] == 0) {
									maxj = j;
									break;
								} else if(lrufif[maxj] < lrufif[j]) {
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
					lrufif[i]++;
				}
			} else if(cj == 1) {
				for(int i = 2; i < 4; i++) {

					if(mc[i][4] == bp) {

						if(operacion == TipoOperacion.ST) { //Notifica que ha modificado.
							mc[i][1] = 1;
						}

						numAciertos++; //Se incrementan los aciertos para la tasa de aciertos por cada dirección.

						acierto = true; //El estado de la operación se cambia si hay acierto o fallo.

						if(politicaRemplazo == PoliticaRemplazo.LRU) { //Si se utiliza LRU.
							lrufif[i] = 0;
						} else {
							lrufif[i]++;
						}

						break;
					} else {
						if(i == 3) {
							acierto = false;
							int maxj = 2; //Indice del más mayor.
							for(int j = 2; j < 4; j++) {
								if(mc[j][0] == 0) {
									maxj = j;
									break;
								} else if(lrufif[maxj] < lrufif[j]) {
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
					lrufif[i]++;
				}
			} else if(cj == 2) {
				for(int i = 4; i < 6; i++) {

					if(mc[i][4] == bp) {

						if(operacion == TipoOperacion.ST) { //Notifica que ha modificado.
							mc[i][1] = 1;
						}

						numAciertos++; //Se incrementan los aciertos para la tasa de aciertos por cada dirección.

						acierto = true; //El estado de la operación se cambia si hay acierto o fallo.

						if(politicaRemplazo == PoliticaRemplazo.LRU) { //Si se utiliza LRU.
							lrufif[i] = 0;
						} else {
							lrufif[i]++;
						}

						break;
					} else {
						if(i == 5) {
							acierto = false;

							int maxj = 4; //Indice del más mayor.
							for(int j = 4; j < 6; j++) {
								if(mc[j][0] == 0) {
									maxj = j;
									break;
								} else if(lrufif[maxj] < lrufif[j]) {
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
					lrufif[i]++;
				}
			} else if(cj == 3) {
				for(int i = 6; i < mc.length; i++) {
					if(mc[i][4] == bp) {

						if(operacion == TipoOperacion.ST) { //Notifica que ha modificado.
							mc[i][1] = 1;
						}

						numAciertos++; //Se incrementan los aciertos para la tasa de aciertos por cada dirección.

						acierto = true; //El estado de la operación se cambia si hay acierto o fallo.

						if(politicaRemplazo == PoliticaRemplazo.LRU) { //Si se utiliza LRU.
							lrufif[i] = 0;
						} else {
							lrufif[i]++;
						}

						break;
					} else {
						if(i == 7) {
							acierto = false;
							int maxj = 6; //Indice del más mayor.
							for(int j = 6; j < mc.length; j++) {
								if(mc[j][0] == 0) {
									maxj = j;
									break;
								} else if(lrufif[maxj] < lrufif[j]) {
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
					lrufif[i]++;
				}
			} else if(8/tamCache == 8) {
				if(mc[cj][4] == bp) {

					if(operacion == TipoOperacion.ST) { //Notifica que ha modificado.
						mc[cj][1] = 1;
					}

					numAciertos++;
					acierto = true;
				} else {
					acierto = false;
					mc[cj][0] = 1;
					mc[cj][1] = 0;
					mc[cj][2] = bp/8;
					mc[cj][4] = bp;
				}
			}
		}
		
		totalCiclos += getCiclos(acierto, dirty);
		
	}

	public void calculaTiempoTot() {
		System.out.printf("Referencias: %d -- Aciertos: %d -- Tasa de aciertos, h = %.2f\n", numReferencias, numAciertos, getTasaAcierto());
		System.out.println("Tiempo total = " + getTotalCiclos());
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

	/**
	 * Calcula el porcentaje de aciertos en el numero de referecias a la memoria cache que se han hecho
	 * 
	 * @return La tasa de acierto
	 */
	public float getTasaAcierto() {
		if (numReferencias > 0) {
			return (numAciertos*100)/numReferencias;
		}
		return 0;
	}

	/**
	 * 
	 * Calcular el numeros de ciclos de la operacion
	 * 
	 * @param acierto {@code true}, si el bloque esta en cache, {@code false}, si es al contrario
	 * @param dirty {@code true}, si es necesario reemplazar el bloque, {@code false}, si es al contrario.
	 * 					(Si {@code acierto} es {@code true}, el parametro {@code dirty} no afecta al resultado)
	 * 
	 * @return numeros de ciclos
	 */
	public int getCiclos(boolean acierto, boolean dirty) {

		int ciclos = getTiempoMemCache();

		if(!acierto) {

			ciclos += getTiempoBloque();

			if(dirty) { 
				ciclos += getTiempoBloque();
			}

		} 

		return ciclos;
	}

	/**
	 * Obtener los ciclos que ha tardado en total
	 * 
	 * @return los ciclos tardados
	 */
	public int getTotalCiclos() {
		return totalCiclos;
	}

	/**
	 * Poner los ciclos que ha tardado en total
	 * 
	 * @param totalCiclos los ciclos tardados
	 */
	public void setTotalCiclos(int totalCiclos) {
		this.totalCiclos = totalCiclos;
	}

	/**
	 * Comprobar si el bloque esta modificado o no
	 * 
	 * @return {@code true}, si esta modificado, {@code false}, si es al contrario
	 */
	public boolean isDirty(int dir) {
		int bp = calculaBloqPrin(calculaPal(dir));
		int cj = calculaConj(bp, 8/tamCache);
		return mc[cj][1] == 1;
	}

}
