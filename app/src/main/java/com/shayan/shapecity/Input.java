package com.shayan.shapecity;

import android.view.MotionEvent;

import com.nurverek.firestorm.FSBounds;
import com.nurverek.firestorm.FSBoundsCuboid;
import com.nurverek.firestorm.FSInput;
import com.nurverek.firestorm.FSMesh;
import com.nurverek.vanguard.VLMath;

public final class Input{

    private static final float[] CACHE = new float[3];

    protected static FSInput.Entry closestPoint;
    protected static float closestDistance;

    protected static void activateInputListeners(FSMesh targetlayer, Runnable onactivated){
        int size = targetlayer.size();
        FSInput.clear(FSInput.TYPE_TOUCH);

        for(int i = 0; i < size; i++){
            FSInput.add(FSInput.TYPE_TOUCH, new FSInput.Entry(targetlayer, i, new FSInput.CollisionListener(){

                @Override
                public int activated(FSBounds.Collision results, FSInput.Entry entry, int boundindex, MotionEvent e1, MotionEvent e2, float f1, float f2, float[] near, float[] far){
                    if(e1.getAction() == MotionEvent.ACTION_UP){
                        FSBoundsCuboid bounds = (FSBoundsCuboid)entry.mesh.instance(entry.instanceindex).schematics().inputBounds().get(boundindex);

                        float[] coords = bounds.offset().coordinates();

                        CACHE[0] = coords[0] + VLMath.clamp(near[0], -bounds.getHalfWidth(), bounds.getHalfWidth());
                        CACHE[1] = coords[1] + VLMath.clamp(near[1], -bounds.getHalfHeight(), bounds.getHalfHeight());
                        CACHE[2] = coords[2] + VLMath.clamp(near[2], -bounds.getHalfDepth(), bounds.getHalfDepth());

                        float distance = VLMath.euclideanDistance(CACHE, 0, near, 0, 3);

                        if(closestDistance > distance){
                            closestDistance = distance;
                            closestPoint = entry;
                        }
                    }

                    return FSInput.INPUT_CHECK_CONTINUE;
                }
            }));
        }

        FSInput.setMainListener(new FSInput.Listener(){

            @Override
            public void preProcess(){
                closestPoint = null;
                closestDistance = Float.MAX_VALUE;
            }

            @Override
            public void postProcess(){
                if(closestPoint != null){
                    onactivated.run();
                }
            }
        });
    }
}
