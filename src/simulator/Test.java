package simulator;

import java.util.Arrays;

public class Test {
	
	//Cambiame para modificar el numero de bloques en la memori cache
	private static final int BLOQ_MC = 8;

	public static void main(String[] args) {
		
		int[][] mc = new int[BLOQ_MC][5];
		
		System.out.println(Arrays.deepToString(mc));
		
		//Inicializar a -1 la ultima columna de cada fila
		int lastIndex = mc[0].length - 1;
		for (int[] i : mc) {
			i[lastIndex] = -1;
		}
		
		//Valor de prueba
		mc[2][4] = 35;
		
		System.out.println(Arrays.deepToString(mc));
		
		System.out.println("# Conjuntos de 1 bloque:");
		imprimeTabla(mc, 1);
		
		System.out.println("# Conjuntos de 2 bloques:");
		imprimeTabla(mc, 2);
		
		System.out.println("# Conjuntos de 4 bloques:");
		imprimeTabla(mc, 4);
		
		System.out.println("# Conjuntos de 8 bloques:");
		imprimeTabla(mc, 8);

	}
	
	public static void imprimeTabla(int[][] mc, int tamConj) {
		
		int bloq = 0;
		
		StringBuilder sb = new StringBuilder();
		sb.append("ocup\tmod\ttag\trem\t||\tbloque\n");
		sb.append("----------------------------------------------------\n");
		
		int size = mc[0].length - 1;
		for (int i = 0; i < mc.length; i++) {
			for (int j = 0; j < mc[i].length; j++) {
				
				if(j < size) { //Si NO es la ultima columna
					sb.append(mc[i][j]+"\t");
				} else {//Si ES la ultima columna
					sb.append("||\t");
					if(mc[i][size] != -1) { //Si NO es -1 (NO esta vacio)
						sb.append("b" + mc[i][size]);
					} else {//Si ES -1 (ESTA vacio)
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

}
