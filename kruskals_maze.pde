
class Kruskals_Maze
{
  
  boolean finished = false;
  int maze_width;      //size of the window in pixels
  int side;    //size of sides in pixels
  int columns;
  int rows;
  int set_count = 0;
  int end_counter = 0;
  Cell[][] cell_object;    //allocates memory for an array of cell objects
  Cell[][] set_of_cells;
  
  //constructor, doesn't pass any arguments
  Kruskals_Maze(){}
  
  void begin(int maze_width, int square_size)
  {
    this.maze_width = maze_width;
    this.side = square_size;    //size of sides in pixels
    this.columns = floor(this.maze_width / this.side);
    this.rows    = floor(this.maze_width / this.side);
    this.cell_object = new Cell[columns][rows];
    
    int sets = this.rows * this.columns;
    int cells_in_set = this.rows * this.columns;
    this.set_of_cells = new Cell[sets][cells_in_set];
    this.set_count =0;
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      {  //create cell objects for every new square in the grid using rows and columns
        this.cell_object[i][j] = new Cell(i, j, this.side);      //assigns column i and row j to a cell object
        //this.set_of_cells[i][j] = this.cell_object[i][j];
        //each cell is in its own set. This is identified by a number
        this.cell_object[i][j].set_number = this.set_count;
        this.set_count ++;
      }
    }
  }
  void generate_maze()
  {
    k_start = Instant.now();
    finished = false;
    while (finished == false)
    {
      //pick a random cell
      int r_column = floor(random(0, this.columns));
      int r_row = floor(random(0, this.rows));
      //System.out.println("set_count " + this.set_count);
      
      
      //pick a random neighbour
      
      Cell current_cell = this.cell_object[r_row][r_column];
      current_cell.visited = true;
      Cell neighbour_cell = this.get_random_neighbour(current_cell);
      if (neighbour_cell != null)
      {
        neighbour_cell.visited = true;
        //if the cells divided by a wall are distinct sets
        if (neighbour_cell.set_number != current_cell.set_number)
        {
          this.end_counter ++;
          //remove the wall
          this.remove_walls(current_cell, neighbour_cell);
          //we must replace all the old cell sets to the new one
          //add neighbour to the current set
          int neighbour_set = neighbour_cell.set_number;
          neighbour_cell.set_number = current_cell.set_number;
          this.replace_all_cells_with_matching_set(neighbour_set, current_cell.set_number);
  
          //this.set_count--;
          if ((this.rows * this.columns)-1 == this.end_counter)
          {
            finished = true;
            //benchmark
            k_end = Instant.now();
          }
        }
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
  
  Cell get_random_neighbour(Cell cell)
  {
    //ArrayList<Cell[]> neighbours;
    Cell[] neighbours = new Cell[5];
    int count_neighbours = 0;
    
    //check for out of bounds
    if (cell.j-1 >= 0)
    {
      neighbours[count_neighbours] = cell_object[cell.i][cell.j-1];
      count_neighbours++;
    }
    if (cell.i+1 <= columns -1) 
    {
      neighbours[count_neighbours] = cell_object[cell.i+1][cell.j];
      count_neighbours++;
    }
    if (cell.j +1 <= rows -1)
    {
      neighbours[count_neighbours] = cell_object[cell.i][cell.j+1];
      count_neighbours++;
    }
    if (cell.i -1 >= 0)
    {
      neighbours[count_neighbours] = cell_object[cell.i-1][cell.j];
      count_neighbours++;
    }
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
  
  void replace_all_cells_with_matching_set(int set_to_find, int set_to_swap)
  {
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      {  //create cell objects for every new square in the grid using rows and columns
        if (this.cell_object[i][j].set_number == set_to_find) 
        {
          this.cell_object[i][j].set_number = set_to_swap;
        }
      }
    }
  }
  
  void remove_walls(Cell cell, Cell neighbour)
  {
    if (cell.i > neighbour.i)
    {
      //remove cells left wall
      cell.wall[3] = false;
      neighbour.wall[1] = false;
    }
    if (cell.i < neighbour.i)
    {
      //remove cells right wall
      cell.wall[1] = false;
      neighbour.wall[3] = false;
    }
    if (cell.j > neighbour.j)
    {
      //remove cells left wall
      cell.wall[0] = false;
      neighbour.wall[2] = false;
    }
    if (cell.j < neighbour.j)
    {
      //remove cells right wall
      cell.wall[2] = false;
      neighbour.wall[0] = false;
    }
  }
  void update()
  {
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      { 
        this.cell_object[i][j].drawBox();
      }
    }
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      { 
        this.cell_object[i][j].drawWalls();
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
  
}