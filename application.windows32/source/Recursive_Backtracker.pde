import java.time.Duration;
import java.time.Instant;


class Recursive_Backtracker
{
  boolean finished = false;
  int maze_width;      //size of the window in pixels
  int side;    //size of sides in pixels
  int columns;
  int rows;
  Cell[][] cell_object;    //allocates memory for an array of cell objects

  
  Recursive_Backtracker()
  {
  }
  void begin(int maze_width, int square_size)
  {
    this.maze_width = maze_width;
    this.side = square_size;    //size of sides in pixels
    this.columns = floor(this.maze_width / this.side);
    this.rows    = floor(this.maze_width / this.side);
    this.cell_object = new Cell[columns][rows];
    
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      {  //create cell objects for every new square in the grid using rows and columns
        this.cell_object[i][j] = new Cell(i, j, this.side);      //assigns column i and row j to a cell object
      }
    }
  }
  
  void setOffset(int offset_x_in_pixels, int offset_y_in_pixels)
  {    
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      {  //create cell objects for every new square in the grid using rows and columns
        this.cell_object[i][j].setOffsets(offset_x_in_pixels, offset_y_in_pixels);      //assigns column i and row j to a cell object
      }
    }
  }
  void setWallColour(int r, int g, int b, int a)
  {
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      {  //create cell objects for every new square in the grid using rows and columns
        this.cell_object[i][j].setWallColour(r, g, b, a);      //assigns column i and row j to a cell object
      }
    }
  }
  
  void setColourGradient(int start_r, int start_g, int start_b, int factor_r, int factor_g, int factor_b)
  {
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      { 
        Cell current = this.cell_object[i][j];
        float ratio_i = float(current.i) / this.columns;
        float ratio_j = float(current.j) / this.rows;
        int r = int(start_r + (ratio_j * factor_r)); 
        int g = int(start_g + (ratio_i * factor_g)); 
        int b = int(start_b + (ratio_i * factor_b)); 
        this.cell_object[i][j].setBackgroundColour(r, g, b, 255);
      }
    }
  }
  
  void remove_walls(Cell cell, Cell neighbour)
  {
    //finding the difference between the x coordinates of cell and neighbour (which is given by i)
    int x = cell.i - neighbour.i;
    if (x == 1)  //if the wall to be removed is on the left of cell
    {
      cell.wall[3] = false;    //remove the left wall of cell
      neighbour.wall[1] = false;    //remove the right wall of neighbour
    }
    else if (x == -1)
    {
      cell.wall[1] = false;
      neighbour.wall[3] = false;
    }
    int y = cell.j - neighbour.j;
    if (y == 1)  //if the wall to be removed is the top of cell
    {
      cell.wall[0] = false;    //remove the top wall of cell
      neighbour.wall[2] = false;    //remove the bottom wall of neighbour
    }
    else if (y == -1)
    {
      cell.wall[2] = false;
      neighbour.wall[0] = false;
    }
  }
  //checks if the current cell has a neighbour, then returns a random one as an object
  Cell checkNeighbours(Cell cell)
  {
    //ArrayList<Cell[]> neighbours;
    Cell[] neighbours = new Cell[50];
    int count_neighbours = 0;
    
    //check for out of bounds
    //System.out.print("Found:\t");
    if (cell.j-1 >= 0)
    {
      if (cell_object[cell.i][cell.j-1].visited == false)
      {
        neighbours[count_neighbours] = cell_object[cell.i][cell.j-1];
        count_neighbours++;
        //System.out.print("top\t");
      }
    }
    if (cell.i+1 <= columns -1) 
    {
      if (cell_object[cell.i+1][cell.j].visited == false)
      {
        neighbours[count_neighbours] = cell_object[cell.i+1][cell.j];
        count_neighbours++;
        //System.out.print("right\t");
      }
    }
    if (cell.j +1 <= rows -1)
    {
      if (cell_object[cell.i][cell.j+1].visited == false)
      {
        neighbours[count_neighbours] = cell_object[cell.i][cell.j+1];
        count_neighbours++;
        //System.out.print("bottom\t");
      }
    }
    if (cell.i -1 >= 0)
    {
      if (cell_object[cell.i-1][cell.j].visited == false)
      {
        neighbours[count_neighbours] = cell_object[cell.i-1][cell.j];
        count_neighbours++;
        //System.out.print("left");
      }
    }
    //System.out.println("");
    //if there are neighbours in the array, pick one at random
    if (count_neighbours > 0)
    {  //generate random number
      //Random rand = new Random();
      int r = floor (random(0, count_neighbours));
      //int r = rand.nextInt( neighbours.length + 1);
      //System.out.println("r is \t" + r);
      //System.out.println("n \t" + neighbours[r]);
      return neighbours[r];
    }
    else
    {
      return null;
    }
  }
  void generate_maze()
  {
    //stack containing the cells connected to the maze
    Stack<Cell> stack_of_cells = new Stack<Cell>();
    Cell current_cell;
    //starting at the top left corner, set this as our current cell
    current_cell = cell_object[0][0];
    
    
    //delay(1);
    start = Instant.now();
    while (!finished)
    {
      //delay(10);
      //mark the cell as being visited
      current_cell.visited = true;
      Cell next;
      //step 1 of the algorithm
      next = checkNeighbours(current_cell);
      //System.out.println(next);
      if (next != null)
      {
        //next.visited = true;
        //STEP 2 - push current cell to stack
        stack_of_cells.push(current_cell);
        
        //step3
        remove_walls(current_cell, next);    //remove the walls between the current and neighbour cells
        //step 4
        current_cell = next;
      }
      else if (stack_of_cells.empty() == false)    //back track if there are no neighbours
      {
        current_cell = stack_of_cells.pop();      //to back track, a cell is removed from stack
        //current_cell = stack_of_cells.peek();
        
      }
      else
      {  
        finished = true;
        //benchmark
        end = Instant.now();
        /*
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Recursive Backtracker " + rows + " x " + columns);
        System.out.println("Time Taken: " + timeElapsed.toMillis() + " ms");
        */
      }
    }
  }
  //refreshes the display with the maze
  void update()
  {
    for (int j=0; j < this.rows; j++)
      {
        for (int i=0; i < this.columns; i++)
        {  //create cell objects for every new square in the grid using rows and columns
          //displays the cell object
          this.cell_object[i][j].drawBox();   
        }
    }
    for (int j=0; j < this.rows; j++)
      {  
        for (int i=0; i < this.columns; i++)
        {  //create cell objects for every new square in the grid using rows and columns
          //displays the cell object
          this.cell_object[i][j].drawWalls();   
        }
     }
  }
  
}