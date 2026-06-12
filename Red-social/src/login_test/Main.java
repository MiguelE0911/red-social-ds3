package login_test;

import social.ChatPanel;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
    	boolean cerroSesion;
    	do {
    	    Login login = new Login(null);
    	    login.setVisible(true);

    	    if (!login.isLoginExitoso()) {
    	        JOptionPane.showMessageDialog(null,
    	                "No se pudo iniciar sesión. El programa terminará.",
    	                "Acceso denegado", JOptionPane.ERROR_MESSAGE);
    	        break;
    	    }

    	    ChatPanel chat = new ChatPanel(null, login.getUsuarioId(), login.getUsuarioNombre(), login.getUsuarioRol());
    	    chat.setVisible(true);
    	    cerroSesion = chat.isCerroPorSesion();

    	} while (cerroSesion);
    }
}