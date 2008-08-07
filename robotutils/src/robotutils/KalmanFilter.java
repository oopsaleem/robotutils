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
 * A lightweight Kalman filter implementation based on the Wikipedia article.
 * @author Prasanna Velagapudi <pkv@cs.cmu.edu>
 */
public class KalmanFilter {
    
    /**
     * Predicted state.
     */
    protected Matrix x;
    
    /**
     * Predicted state covariance.
     */
    protected Matrix P;
    
    /**
     * Optimal Kalman gain.
     */
    protected Matrix K;
        
    /**
     * Default process model (state transition matrix).
     */
    protected Matrix myF;
    
    /**
     * Default process noise.
     */
    protected Matrix myQ;
    
    /**
     * Default control model (maps control vector to state space).
     */
    protected Matrix myB;
    
    /**
     * Default observation model (maps observations to state space).
     */
    protected Matrix myH;
    
    /**
     * Default observation noise.
     */
    protected Matrix myR;
    
    /**
     * Constructs a Kalman filter with no default motion and observation
     * models.
     * @param x the initial state estimate.
     * @param P the initial state covariance.
     */
    public KalmanFilter(Matrix x, Matrix P) {
        this.x = x;
        this.P = P;
    }
    
    /**
     * Constructs a Kalman filter with a default motion model and no default
     * observation model.
     * @param x the initial state estimate.
     * @param P the initial state covariance.
     * @param F the default process model.
     * @param Q the default process noise.
     * @param B the default control model.
     */
    public KalmanFilter(Matrix x, Matrix P, 
            Matrix F, Matrix Q, Matrix B) {
        this(x, P);
        
        myF = F;
        myQ = Q;
        myB = B;
    }
    
    /**
     * Constructs a Kalman filter with a default motion model and no default
     * observation model.
     * @param x the initial state estimate.
     * @param P the initial state covariance.
     * @param F the default process model.
     * @param Q the default process noise.
     * @param B the default control model.
     * @param H the default observation model.
     * @param R the default observation noise.
     */
    public KalmanFilter(Matrix x, Matrix P, 
            Matrix F, Matrix Q, Matrix B,
            Matrix H, Matrix R) {
        this(x, P, F, Q, B);
        
        myH = H;
        myR = R;
    }
    
    /**
     * Uses the previous state estimate and the default motion model to produce 
     * an estimate of the current state.
     * @param u the current control input.
     */
    public void predict(Matrix u) {
        predict(myF, myQ, myB, u);
    }
    
    /**
     * Uses the previous state estimate and the provided motion model to produce
     * an estimate of the current state.
     * @param F the process model.
     * @param Q the process noise.
     * @param B the control model.
     * @param u the current control input.
     */
    public void predict(Matrix F, Matrix Q, Matrix B, Matrix u) {
        x = F.times(x).plus(B.times(u));
        P = F.times(P).times(F.transpose()).plus(Q);
    }

    /**
     * Current measurement information is used to refine the state estimate 
     * using the default observation model.
     * @param z the current measurement.
     */
    public void update(Matrix z) {
        update(myH, myR, z);
    }
    
    /**
     * Current measurement information is used to refine the state estimate 
     * using the provided observation model.
     * @param H the observation model.
     * @param R the observation noise.
     * @param z the current measurement.
     */
    public void update(Matrix H, Matrix R, Matrix z) {
        Matrix I = Matrix.identity(K.getRowDimension(), H.getColumnDimension());
        Matrix y = z.minus(H.times(x));
        Matrix S = H.times(P).times(H.transpose()).plus(R);
        K = P.times(H.transpose()).times(S.inverse());
        x = x.plus(K.times(y));
        P = I.minus(K.times(H)).times(P);
    }
    
    /**
     * Sets the current state estimate.
     * @param x the new state estimate.
     */
    public void setState(Matrix x) {
        this.x = x;
    }
    
    /**
     * Gets the current state estimate.
     * @return the current state estimate.
     */
    public Matrix getState() {
        return (Matrix)x.clone();
    }
    
    /**
     * Sets the current state covariance.
     * @param P the new state covariance.
     */
    public void setStateCov(Matrix P) {
        this.P = P;
    }
    
    /**
     * Gets the current state covariance.
     * @return the current state covariance.
     */
    public Matrix getStateCov() {
        return (Matrix)P.clone();
    }
    
    /**
     * Gets the most recently computed Kalman gain.
     * @return the current Kalman gain.
     */
    public Matrix getKalmanGain() {
        return (Matrix)K.clone();
    }
    
    /**
     * Sets the default process model.
     * @param F the new process model. 
     */
    public void setProcessModel(Matrix F) {
        this.myF = F;
    }
    
    /**
     * Gets the default process model.
     * @return the default process model.
     */
    public Matrix getProcessModel() {
        return (Matrix)myF.clone();
    }
    
    /**
     * Sets the default process noise.
     * @param Q the new process noise.
     */
    public void setProcessNoise(Matrix Q) {
        this.myQ = Q;
    }
    
    /**
     * Gets the default process noise.
     * @return the default process noise.
     */
    public Matrix getProcessNoise() {
        return (Matrix)myQ.clone();
    }
    
    /**
     * Sets the default control model.
     * @param B the new control model.
     */
    public void setControlModel(Matrix B) {
        this.myB = B;
    }
    
    /**
     * Gets the default control model.
     * @return the default control model.
     */
    public Matrix getControlModel() {
        return (Matrix)myB.clone();
    }
    
    /**
     * Sets the default observation model.
     * @param H the new observation model.
     */
    public void setObsModel(Matrix H) {
        this.myH = H;
    }
    
    /**
     * Gets the default observation model.
     * @return the default observation model.
     */
    public Matrix getObsModel() {
        return (Matrix)myH.clone();
    }
    
    /**
     * Sets the default observation noise.
     * @param R the new observation noise.
     */
    public void setObsNoise(Matrix R) {
        this.myR = R;
    }
    
    /**
     * Gets the default observation noise.
     * @return the default observation noise.
     */
    public Matrix getObsNoise() {
        return (Matrix)myR.clone();
    }    
    
}
