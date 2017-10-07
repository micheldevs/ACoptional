package simulator;

import java.util.InputMismatchException;
import java.util.Scanner;

import simulator.Table.TipoOperacion;

public class Main {

	public static void main(String[] args) {

		int tpal = 0, tb = 0, tc = 0, pr = 0, dirBloq = 0;

		System.out.println("Simulador de cache simple - Creado por: Ferran Tudela García y Miguel Ángel Blanco Fernández\n");

		/*
		 * En cuanto salga del bloque 'try', se cerrara automaticamente el scanner
		 * 
		 * try-with-resources (http://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)
		 */
		try(Scanner sc = new Scanner(System.in)) {

			do {

				System.out.print("Escriba de que tamaño quiere que sean las palabras: 4 - 8 bytes\n> ");

				try {
					tpal = sc.nextInt();

					if(tpal != 4 && tpal != 8) {
						System.out.print("\nValor no válido. ");
					}

				} catch (InputMismatchException e) {
					System.out.println("El valor introducido no es un numero.");
				}

			} while(tpal != 4 && tpal != 8);

			System.out.println();


			do {

				System.out.print("Escriba de que tamaño quiere que sean los bloques: 32 - 64 bytes\n> ");

				try {

					tb = sc.nextInt();

					if(tb != 32 && tb != 64) {
						System.out.print("\nValor no válido. ");
					}

				} catch (InputMismatchException e) {
					System.out.println("El valor introducido no es un numero.");
				}

			} while(tb != 32 && tb != 64);

			System.out.println();


			do {

				System.out.print("Escriba de que tamaño quiere que sean los conjuntos: 1 - 2 - 4 - 8 conjuntos\n> ");

				try {

					tc = sc.nextInt();

					if(tc != 1 && tc != 2 && tc != 4 && tc != 8) {
						System.out.print("\nValor no válido. ");
					}

				} catch (InputMismatchException e) {
					System.out.println("El valor introducido no es un numero.");
				}

			} while(tc != 1 && tc != 2 && tc != 4 && tc != 8);

			System.out.println();


			do {

				System.out.print("Escriba política de remplazo deseada: 0 (FIFO) - 1 (LRU)\n> ");

				try {

					pr = sc.nextInt();

					if(pr != 0 && pr != 1) {
						System.out.print("\nValor no válido. ");
					}

				} catch (InputMismatchException e) {
					System.out.println("El valor introducido no es un numero.");
				}

			} while(pr != 0 && pr != 1);

			System.out.println("\n");

			Table table = new Table(tpal, tb, tc);

			do {

				do {

					System.out.print("Escriba dirección byte: (-1 para salir)\n> ");
					
					try {
						
						dirBloq = sc.nextInt();
						
						//TODO: Comprobar que la direccion existe en la MP (no es demasiado grande)
						if(dirBloq < 0 && dirBloq != -1) {
							System.out.print("\nValor no válido. ");
						}
						
					} catch (InputMismatchException e) {
						System.out.println("El valor introducido no es un numero.");
					}

				} while(dirBloq < 0 && dirBloq != -1);
				
				System.out.println();

				if (dirBloq != -1) {

					TipoOperacion op = null;

					do {

						System.out.print("Load (0) / Store (1)\n> ");

						switch (sc.nextInt()) {
						case 0: // Load
							op = TipoOperacion.LD;
							break;

						case 1: // Store
							op = TipoOperacion.ST;
							break;

						default:
							System.out.print("\nValor no válido. ");
							break;
						}

					} while(op == null);
					
					System.out.println();

					table.colocaBloq(dirBloq, op);
					table.imprimirResultado(dirBloq, 8/tc);
					table.imprimirEstado(dirBloq); // TODO: Comprobar que este bien
					table.imprimeTabla(table.getMc(), tc);
				}

			} while(dirBloq != -1);
			
			System.out.println();

			table.calculaTiempoTot();

		}

	}

}
