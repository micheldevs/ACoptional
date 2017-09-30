package simulator;

import java.util.Scanner;

public class Main {
	public void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		int pal, tb, tc, pr, db = 0, op;

		System.out.println("Simulador de cache simple - Creado por: Ferran Tudela García y Miguel Ángel Blanco Fernández");
		System.out.println("Escriba de que tamaño quiere que sean las palabras: 4-8 bytes >");
		pal = sc.nextInt();
		System.out.println("Escriba de que tamaño quiere que sean los bloques: 32-64 bytes >");
		tb = sc.nextInt();
		System.out.println("");
		System.out.println("Escriba de que tamaño quiere que sean los conjuntos: 1-2-4-8 conjuntos >");
		tc = sc.nextInt();
		System.out.println("Escriba política de remplazo deseada: 0 (FIFO) - 1 (LRU) >");
		pr = sc.nextInt();
		System.out.println("");
		while(db != -1) {
			System.out.println("Escriba dirección byte: (-1 para salir) >");
			db = sc.nextInt();
			System.out.println("Load (0) / Store (1) >");
			op = sc.nextInt();
		}
	}
}
