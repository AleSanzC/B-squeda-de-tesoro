package juego;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.Timer;

public class Fantasmas {

    //atributos
    int fanx;
    int fany;
    int tipoFantasma; // Nuevo atributo para identificar el tipo de fantasma
    Timer timer;
    Random aleatorio;
    int direccion;

    public Fantasmas(int x, int y, int tipo) {
        aleatorio = new Random();
        fanx = x;
        fany = y;
        this.tipoFantasma = tipo; // Inicializa el tipo del fantasma
        Juego.mat[fanx][fany] = tipoFantasma;
        direccion = aleatorio.nextInt(4);
        this.movimiento();
    }//contrutor

    public void movimiento() {

         timer = new Timer(190, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Control de movimiento según dirección
                switch (direccion) {
                    case 0: // Izquierda
                        if (fanx > 0 && (Juego.mat[fanx - 1][fany] == 0 || Juego.mat[fanx - 1][fany] == 1)) {
                            actualizarPosicion(fanx - 1, fany);
                        } else {
                            direccion = aleatorio.nextInt(4); // Cambiar dirección
                        }
                        break;

                    case 1: // Derecha
                        if (fanx < 14 && (Juego.mat[fanx + 1][fany] == 0 || Juego.mat[fanx + 1][fany] == 1)) {
                            actualizarPosicion(fanx + 1, fany);
                        } else {
                            direccion = aleatorio.nextInt(4); // Cambiar dirección
                        }
                        break;

                    case 2: // Arriba
                        if (fany > 0 && (Juego.mat[fanx][fany - 1] == 0 || Juego.mat[fanx][fany - 1] == 1)) {
                            actualizarPosicion(fanx, fany - 1);
                        } else {
                            direccion = aleatorio.nextInt(4); // Cambiar dirección
                        }
                        break;

                    case 3: // Abajo
                        if (fany < 14 && (Juego.mat[fanx][fany + 1] == 0 || Juego.mat[fanx][fany + 1] == 1)) {
                            actualizarPosicion(fanx, fany + 1);
                        } else {
                            direccion = aleatorio.nextInt(4); // Cambiar dirección
                        }
                        break;
                }
            }
        });
        timer.start(); // Inicia el movimiento del fantasma
    }

    private void actualizarPosicion(int nuevoX, int nuevoY) {
        // Limpia la posición anterior
        Juego.mat[fanx][fany] = Juego.matAux[fanx][fany];

        // Actualiza la nueva posición
        fanx = nuevoX;
        fany = nuevoY;
        Juego.mat[fanx][fany] = tipoFantasma;

        // Actualiza la matriz gráfica
        Juego.pintarMatriz();
    }

}
