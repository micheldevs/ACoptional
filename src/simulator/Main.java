package simulator;

import java.util.Scanner;

import simulator.Table.TipoOperacion;

public class Main {
	
	public static void main(String[] args) {

		int tpal, tb, tc, pr, dirBloq = 0;
		TipoOperacion op;
		
		System.out.println("Simulador de cache simple - Creado por: Ferran Tudela García y Miguel Ángel Blanco Fernández\n");
		
		/*
		 * En cuanto salga del bloque 'try', se cerrara automaticamente el scanner
		 * 
		 * try-with-resources (http://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)
		 */
		try(Scanner sc = new Scanner(System.in)) {
			
			do {
				
				System.out.print("Escriba de que tamaño quiere que sean las palabras: 4 - 8 bytes\n> ");
				tpal = sc.nextInt();
				
				if(tpal != 4 && tpal != 8) {
					System.out.print("\nValor no válido. ");
				}
				
			} while(tpal != 4 && tpal != 8);
			
			System.out.println("\n");
			
			
			do {
				
				System.out.println("Escriba de que tamaño quiere que sean los bloques: 32 - 64 bytes\n> ");
				tb = sc.nextInt();
				
				if(tb != 32 && tb != 64) {
					System.out.print("\nValor no válido. ");
				}
				
			} while(tb != 32 && tb != 64);
			
			System.out.println("\n");
			
			
			do {
				
				System.out.println("Escriba de que tamaño quiere que sean los conjuntos: 1 - 2 - 4 - 8 conjuntos\n> ");
				tc = sc.nextInt();
				
				if(tc != 1 && tc != 2 && tc != 4 && tc != 8) {
					System.out.print("\nValor no válido. ");
				}
				
			} while(tc != 1 && tc != 2 && tc != 4 && tc != 8);
			
			System.out.println("\n");
			
			
			do {
				
				System.out.println("Escriba política de remplazo deseada: 0 (FIFO) - 1 (LRU)\n>");
				pr = sc.nextInt();
				
				if(pr != 0 && pr != 1) {
					System.out.print("\nValor no válido. ");
				}
				
			} while(pr != 0 && pr != 1);
			
			System.out.println("\n");
			
			Table table = new Table(tpal, tb, tc);
			
			//TODO: Comprobación de introducción correcta de la dirección
			while(dirBloq != -1) {
				
				System.out.println("\nEscriba dirección byte: (-1 para salir)\n>");
				dirBloq = sc.nextInt();
				
				System.out.println("Load (0) / Store (1)\n>");
				
				switch (sc.nextInt()) {
				case 0: // Load
					op = TipoOperacion.LD;
					break;

				case 1: // Store
					op = TipoOperacion.ST;
					break;

				default:
					// TODO: Controlar numero ajeno a 0 y 1
					op = null;
					break;
				}
				
				table.colocaBloq(dirBloq, op);
				table.imprimirResultado(dirBloq, 8/tc);
				table.imprimirEstado(dirBloq); // TODO: Comprobar que este bien
				table.imprimeTabla(table.getMc(), tc);
				
			}
			
			table.calculaTiempoTot();
		
		}
		
	}
	
}
