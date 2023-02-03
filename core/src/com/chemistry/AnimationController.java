package com.chemistry;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.chemistry.dto.Substance;

import static com.chemistry.ExperimentWindow.*;

public class AnimationController {
    Vector2 startPos;
    Vector2 endPos;
    Texture animatedTexture;
    boolean reachedX = false;
    boolean reachedY = false;

    public AnimationController(Vector2 startPos, Vector2 endPos, Texture texture) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.animatedTexture = texture;
        System.out.println(startPos.x + " - " + startPos.y + "\n" + endPos.x + " - " + endPos.y);
        animationStarted = true;
    }
    public Vector2 Move (){
        if (startPos.x > endPos.x){
            startPos.x--;
        } else if (startPos.x < endPos.x) {
            startPos.x++;
        } else {
            reachedX = true;
        }

        if (startPos.y > endPos.y){
            startPos.y--;
        } else if (startPos.y < endPos.y) {
            startPos.y++;
        } else {
            reachedY = true;
        }

        System.out.println(startPos.x + " - " + startPos.y);

        if (reachedX && reachedY){
            System.out.println("Reached the end");
            reachedX = false;
            reachedY = false;
            animationStarted = false;
            animatedSubstance = new Substance();
        }

        return new Vector2(startPos.x, startPos.y);
    }

    public AnimationController(){

    }
}
