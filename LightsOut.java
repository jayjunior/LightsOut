public class LightsOut {

    public int rows;
    public int cols;
    public long mask;
    public long state;



    private static int anzahlBits(long z) {
        if (z <= 1) return 1;
        return 1 + anzahlBits(z >> 1);
    }

    public LightsOut(int rows, int cols, long mask, long state) {

        long number_of_bits = anzahlBits(state);
        if(rows < 0 || cols < 0 ) throw new IllegalArgumentException("Just don't do That !");

        if ((rows == 0 || cols == 0) && (mask > 0 || state > 0))
            throw new IllegalArgumentException("Just don't do That !");

        this.rows = rows;
        this.cols = cols;
        this.mask = mask;
        this.state = state;


        for (long i = 0; i < number_of_bits; i++) {
            if (i < (long) rows * cols) {
                if (BitOps.isSet(i, mask)) this.state = BitOps.clear(i, this.state);
            } else {
                this.state = BitOps.clear(i, this.state);
            }
        }

    }

    public long getState() {
        return this.state;
    }

    public void toggle(int row, int col) {
        if(row < 0 || row >= this.rows ) throw new IllegalArgumentException("Just don't do That !");
        if(col < 0 || col >= this.cols ) throw new IllegalArgumentException("Just don't do That !");
        long position = ((long) row * this.cols) + col;

        if (position < (long) this.rows * this.cols && !BitOps.isSet(position, mask)) {
            this.state = BitOps.flip(position, this.state);
            long up = position - this.cols;
            long down = position + this.cols;
            long left = position - 1;
            long right = position + 1;


            if (up >= 0 && !BitOps.isSet(up, mask)) this.state = BitOps.flip(up, this.state);// flip up
            if (down < (long) this.rows * this.cols && !BitOps.isSet(down, mask))
                this.state = BitOps.flip(down, this.state);// flip down
            if (position % this.cols == 0) {
                if (right < (long) this.rows * this.cols && !BitOps.isSet(right, mask))
                    this.state = BitOps.flip(right, this.state);//flip right
            } else if ((position + 1) % this.cols == 0) {
                if (left < (long) this.rows * this.cols && !BitOps.isSet(left, mask))
                    this.state = BitOps.flip(left, this.state);// flip left
            } else {
                if (right < (long) this.rows * this.cols && !BitOps.isSet(right, mask))
                    this.state = BitOps.flip(right, this.state);//flip right
                if (left < (long) this.rows * this.cols && !BitOps.isSet(left, mask))
                    this.state = BitOps.flip(left, this.state);// flip left
            }
        }
    }

    public IntegerSequenceMemory solve() {  // schrittweise backtracking !!

        IntegerSequenceMemory solution;

        for(int i = 1 ; i <= this.rows*this.cols ;i++){
            solution = solve0(i,0);
            if(solution != null) return  solution;
        }

        return null;
    }

    private boolean isvalid() {
        return this.state == 0;
    }

    private void apply(int row, int col) {
        toggle(row, col);
    }

    private void revert(int row, int col) {
        toggle(row, col);
    }

    private IntegerSequenceMemory solve0(long maxdepth,int actual_position){

        if(maxdepth <= 0) return null; // if maxdepth is zero then we reached our saldo ... then return null

        IntegerSequenceMemory solution = new IntegerSequenceMemory();


        if (isvalid()) return solution;




        for (int position = actual_position; position < this.rows*this.cols; position++) {
            int row = position / this.cols;
            int col = position % this.cols;
            if (!BitOps.isSet(position, mask)) {
                apply(row, col);

                solution = solve0(maxdepth-1,position); // going down recursive tree just till a certain point determined by max-depth
                if (solution != null) {
                    solution.remember(position);
                    revert(row, col);
                    return solution;
                } else {
                    revert(row, col);
                }
            }
        }



        return null; // we reached invalid state where position >= rows*cols ... so there is nothing more to visit from here ... return null ...
    }



}