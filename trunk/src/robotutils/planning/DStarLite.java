/*
 *  The MIT License
 * 
 *  Copyright 2010 Prasanna Velagapudi <psigen@gmail.com>.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package robotutils.planning;

/**
 * This class implements the optimized D*-lite algorithm exactly as described
 * in [Koenig 2002]. D*-lite is an incremental variant of the A* search
 * algorithm, meaning that it can cheaply update itself to account for new
 * obstacles.
 *
 * Source: Koenig, S. and Likhachev, M. 2002. D*lite. In Eighteenth National
 * Conference on Artificial intelligence (Edmonton, Alberta, Canada, July 28 -
 * August 01, 2002). R. Dechter, M. Kearns, and R. Sutton, Eds. American
 * Association for Artificial Intelligence, Menlo Park, CA, 476-483
 *
 * @author Prasanna Velagapudi <psigen@gmail.com>
 */
public class DStarLite {

    public DStarLite() {

    }

    public void calculateKey() {

    }

    public void initialize() {

    }

    public void updateVertex() {

    }

    public void computeShortestPath() {

    }

    public void plan() {
        sLast = sStart;

        initialize();
        computeShortestPath();

        while (!sStart.equals(sGoal)) {
            // if (rhs(sStart) = inf) then there is no known path

            // sStart = argmin_s (c(start, s) + g(s));

            // Move to sStart

            // Scan graph for changed edge costs

            // If any edge costs changed

        }
    }
}
