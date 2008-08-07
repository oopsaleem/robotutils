/*
 * Copyright (c) 2008, Prasanna Velagapudi <pkv@cs.cmu.edu>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE PROJECT AND CONTRIBUTORS ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE PROJECT AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package robotutils;

import Jama.Matrix;

/**
 * An extension of the Kalman filter that allows for the motion models to be 
 * linearized around the current state at each timestep.
 * 
 * Note that directly using the "set" accessors to the model and observation 
 * matrices will have no effect, as these matrices are now generated via the
 * provided linearization functions.
 * @author Prasanna Velagapudi <pkv@cs.cmu.edu>
 */
public class ExtendedKalmanFilter extends KalmanFilter {

    /**
     * Default process model (state transition matrix).
     */
    protected StateDependentFunction myFnF;
    
    /**
     * Default process noise.
     */
    protected StateDependentFunction myFnQ;
    
    /**
     * Default control model (maps control vector to state space).
     */
    protected StateDependentFunction myFnB;
    
    /**
     * Default observation model (maps observations to state space).
     */
    protected StateDependentFunction myFnH;
    /**
     * Default observation noise.
     */
    protected StateDependentFunction myFnR;
    
    /**
     * Constructs an extended Kalman filter with no default motion and 
     * observation models.
     * @param x the initial state estimate.
     * @param P the initial state covariance.
     */
    public ExtendedKalmanFilter(Matrix x, Matrix P) {
        super(x, P);
    }
    
    /**
     * Constructs an extended Kalman filter with a default motion model and no 
     * default observation model.
     * @param x the initial state estimate.
     * @param P the initial state covariance.
     * @param F the default process model.
     * @param Q the default process noise.
     * @param B the default control model.
     */
    public ExtendedKalmanFilter(Matrix x, Matrix P, 
            StateDependentFunction F,
            StateDependentFunction Q,
            StateDependentFunction B) {
        this(x, P);
        
        myFnF = F;
        myFnQ = Q;
        myFnB = B;
    }
    
    /**
     * Constructs an extended Kalman filter with a default motion model and 
     * default observation model.
     * @param x the initial state estimate.
     * @param P the initial state covariance.
     * @param F the default process model.
     * @param Q the default process noise.
     * @param B the default control model.
     * @param H the default observation model.
     * @param R the default observation noise.
     */
    public ExtendedKalmanFilter(Matrix x, Matrix P, 
            StateDependentFunction F, 
            StateDependentFunction Q, 
            StateDependentFunction B,
            StateDependentFunction H, 
            StateDependentFunction R) {
        this(x, P, F, Q, B);
        
        myFnH = H;
        myFnR = R;
    }
    
    /**
     * Uses the previous state estimate and the default motion model to produce 
     * an estimate of the current state.
     * @param u the current control input.
     */
    @Override
    public void predict(Matrix u) {
        Matrix F = myF;
        Matrix Q = myQ;
        Matrix B = myB;
        
        if (myFnF != null) {
            myFnF.eval(x);
        }
        
        if (myFnQ != null) {
            myFnQ.eval(x);
        }
        
        if (myFnB != null) {
            myFnB.eval(x);
        }
        
        predict(F, Q, B, u);
    }
    
    /**
     * @see KalmanFilter#predict(Jama.Matrix, Jama.Matrix, Jama.Matrix, Jama.Matrix)
     */
    public void predict(StateDependentFunction F, 
            StateDependentFunction Q, 
            StateDependentFunction B, 
            Matrix u) {
        predict(F.eval(x), Q.eval(x), B.eval(x), u);
    }
    
    /**
     * @see KalmanFilter#predict(Jama.Matrix, Jama.Matrix, Jama.Matrix, Jama.Matrix)
     */
    public void predict(StateDependentFunction F, 
            StateDependentFunction Q, 
            Matrix B, 
            Matrix u) {
        predict(F.eval(x), Q.eval(x), B, u);
    }
    
    /**
     * @see KalmanFilter#predict(Jama.Matrix, Jama.Matrix, Jama.Matrix, Jama.Matrix)
     */
    public void predict(StateDependentFunction F, 
            Matrix Q, 
            StateDependentFunction B, 
            Matrix u) {
        predict(F.eval(x), Q, B.eval(x), u);
    }
    
    /**
     * @see KalmanFilter#predict(Jama.Matrix, Jama.Matrix, Jama.Matrix, Jama.Matrix)
     */
    public void predict(StateDependentFunction F, 
            Matrix Q, 
            Matrix B, 
            Matrix u) {
        predict(F.eval(x), Q, B, u);
    }
    
    /**
     * @see KalmanFilter#predict(Jama.Matrix, Jama.Matrix, Jama.Matrix, Jama.Matrix)
     */
    public void predict(Matrix F, 
            StateDependentFunction Q, 
            StateDependentFunction B, 
            Matrix u) {
        predict(F, Q.eval(x), B.eval(x), u);
    }
    
    /**
     * @see KalmanFilter#predict(Jama.Matrix, Jama.Matrix, Jama.Matrix, Jama.Matrix)
     */
    public void predict(Matrix F, 
            StateDependentFunction Q, 
            Matrix B, 
            Matrix u) {
        predict(F, Q.eval(x), B, u);
    }
    
    /**
     * @see KalmanFilter#predict(Jama.Matrix, Jama.Matrix, Jama.Matrix, Jama.Matrix)
     */
    public void predict(Matrix F, 
            Matrix Q, 
            StateDependentFunction B, 
            Matrix u) {
        predict(F, Q, B.eval(x), u);
    }
    
    /**
     * @see KalmanFilter#predict(Jama.Matrix, Jama.Matrix, Jama.Matrix, Jama.Matrix) 
     */
    @Override
    public void predict(Matrix F, Matrix Q, Matrix B, Matrix u) {
        super.predict(F, Q, B, u);
        updateMatrices();
    }

    /**
     * @see KalmanFilter#update(Jama.Matrix) 
     */
    @Override
    public void update(Matrix z) {
        Matrix H = myH;
        Matrix R = myR;
        
        if (myFnH != null) {
            myFnH.eval(x);
        }
        
        if (myFnR != null) {
            myFnR.eval(x);
        }
        
        update(H, R, z);
    }
    
    /**
     * @see KalmanFilter#update(Jama.Matrix, Jama.Matrix, Jama.Matrix) 
     */
    public void update(StateDependentFunction H, 
            StateDependentFunction R, 
            Matrix z) {
        update(H.eval(x), R.eval(x), z);
    }
    
    /**
     * @see KalmanFilter#update(Jama.Matrix, Jama.Matrix, Jama.Matrix) 
     */
    public void update(StateDependentFunction H, 
            Matrix R, 
            Matrix z) {
        update(H.eval(x), R, z);
    }
    
    /**
     * @see KalmanFilter#update(Jama.Matrix, Jama.Matrix, Jama.Matrix) 
     */
    public void update(Matrix H, 
            StateDependentFunction R, 
            Matrix z) {
        update(H, R.eval(x), z);
    }
    
    /**
     * @see KalmanFilter#update(Jama.Matrix, Jama.Matrix, Jama.Matrix) 
     */
    @Override
    public void update(Matrix H, Matrix R, Matrix z) {
        super.update(H, R, z);
        updateMatrices();
    }
    
    /**
     * Internal function that caches the last computed model matrices and 
     * stores them in the fields inherited by the KalmanFilter.
     */
    protected void updateMatrices() {
        if (myFnF != null) {
            myF = myFnF.eval(x);
        }
        
        if (myFnQ != null) {
            myQ = myFnQ.eval(x);
        }
        
        if (myFnB != null) {
            myB = myFnB.eval(x);
        }
        
        if (myFnH != null) {
            myH = myFnH.eval(x);
        }
        
        if (myFnR != null) {
            myR = myFnR.eval(x);
        }
    }
    
    /**
     * @see KalmanFilter#setState(Jama.Matrix) 
     */
    @Override
    public void setState(Matrix x) {
        super.setState(x);
        updateMatrices();
    }
    
    /**
     * Sets the default process model.
     * @param F the new process model. 
     */
    public void setProcessModelFn(StateDependentFunction F) {
        this.myFnF = F;
    }
    
    /**
     * Gets the default process model.
     * @return the default process model.
     */
    public StateDependentFunction getProcessModelFn() {
        return myFnF;
    }
    
    /**
     * Sets the default process noise.
     * @param Q the new process noise.
     */
    public void setProcessNoiseFn(StateDependentFunction Q) {
        this.myFnQ = Q;
    }
    
    /**
     * Gets the default process noise.
     * @return the default process noise.
     */
    public StateDependentFunction getProcessNoiseFn() {
        return myFnQ;
    }
    
    /**
     * Sets the default control model.
     * @param B the new control model.
     */
    public void setControlModelFn(StateDependentFunction B) {
        this.myFnB = B;
    }
    
    /**
     * Gets the default control model.
     * @return the default control model.
     */
    public StateDependentFunction getControlModelFn() {
        return myFnB;
    }
    
    /**
     * Sets the default observation model.
     * @param H the new observation model.
     */
    public void setObsModelFn(StateDependentFunction H) {
        this.myFnH = H;
    }
    
    /**
     * Gets the default observation model.
     * @return the default observation model.
     */
    public StateDependentFunction getObsModelFn() {
        return myFnH;
    }
    
    /**
     * Sets the default observation noise.
     * @param R the new observation noise.
     */
    public void setObsNoiseFn(StateDependentFunction R) {
        this.myFnR = R;
    }
    
    /**
     * Gets the default observation noise.
     * @return the default observation noise.
     */
    public StateDependentFunction getObsNoiseFn() {
        return myFnR;
    }
}
