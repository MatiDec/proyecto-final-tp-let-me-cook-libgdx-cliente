package com.hebergames.letmecook;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hebergames.letmecook.pantallas.Pantalla;
import com.hebergames.letmecook.pantallas.PantallaMenu;
import com.hebergames.letmecook.utiles.Render;

public class LetMeCookPrincipal extends Game {

    @Override
    public void create() {
        Render.batch = new SpriteBatch();
        Pantalla.cambiarPantalla(new PantallaMenu());
        this.setScreen(Pantalla.getPantallaActual());
    }

    @Override
    public void render() {
        super.render();

        if(getScreen() != Pantalla.getPantallaActual()) {
            setScreen(Pantalla.getPantallaActual());
        }
    }

    @Override
    public void dispose() {
        Render.batch.dispose();
    }
}
