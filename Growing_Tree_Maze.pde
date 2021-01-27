
class Growing_Tree
{
  boolean finished = false;
  private int start_i = -1;
  private int start_j = -1;
  private int mix_parameter = 0;
  private int split_percentage = 100;
  private boolean init = false;
  private int maze_width = 50;      //size of the window in pixels
  private int side = 40;    //size of sides in pixels
  private int columns;
  private int rows;
  private int off_x;
  private int off_y;
  private long last_change = System.currentTimeMillis();
  Cell[][] cell_object;    //allocates memory for an array of cell objects
  ArrayList<Cell> list_of_cells = new ArrayList<Cell>();
  ArrayList<Cell> cells_part_of_maze = new ArrayList<Cell>();
  //Cell[] list_of_cells;
  
    //ArrayList<Integer> cells_checked = new ArrayList<Integer>();
  Growing_Tree()
  {
  }
  void begin(int maze_width, int side_size)
  {
    this.maze_width = maze_width;
    this.side = side_size;    //size of sides in pixels
    this.columns = floor(this.maze_width / this.side);
    this.rows    = floor(this.maze_width / this.side);
    this.cell_object = new Cell[columns][rows];
    this.init = false;
    list_of_cells.clear();
    cells_part_of_maze.clear();
    this.finished = false;
    //1 start with a grid full of cells
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      {  //create cell objects for every new square in the grid using rows and columns
        this.cell_object[i][j] = new Cell(i, j, this.side);      //assigns column i and row j to a cell object
        //this.cell_object[i][j].setBackgroundColour(225, 15 , 210, 255);
        Cell current = this.cell_object[i][j];
        float ratio_i = float(current.i) / this.columns;
        float ratio_j = float(current.j) / this.rows;
        int r = int(50 + (ratio_j * 80)); 
        int g = int(70 + (ratio_i * 90)); 
        int b = int(150 + (ratio_i * 100)); 
        this.cell_object[i][j].setBackgroundColour(r, g, b, 255);
      }
    }
    this.cell_object[0][0].wall[3] = false;
    this.cell_object[this.columns-1][this.rows-1].wall[1] = false;
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
  
  void setMix(int setting)
  {
    if (setting > 3)         setting = 0;
    else if (setting < 0)    setting = 3;
    this.mix_parameter = setting;
  }
  void setSplitPercentage(int percentage)
  {
    if (percentage > 100)         percentage = 100;
    else if (percentage < 0)      percentage = 0;
    this.split_percentage =       percentage;
  }
  
  boolean build_animation()
  {
    for (int i =0; i < list_of_cells.size(); i ++)
    {
      highlight_neighbours(list_of_cells.get(i));
    }
    draw_grid();
    show_cells_in_maze();
    //executed only once
    if (this.init == false)
    {
      list_of_cells.clear();
      Cell current;
      //use random cell
      if (this.start_i < 0 || this.start_j < 0)
      {
        current = get_random_cell();
      }
      else
      {
        current = cell_object[this.start_i][this.start_j];
      }
      list_of_cells.add(current);    //add that cell to the list
      current.visited = true;
      //current.show();
      current.highlight(10, 255, 50, 185);
      cells_part_of_maze.add(current);
      this.init = true;
    }
    if (list_of_cells.size() > 0)
    {
      Cell random_cell;
      if (this.mix_parameter == 1){
        random_cell = get_cell_old_new(this.split_percentage);
      }
      else if(this.mix_parameter == 2){
        random_cell = get_cell_old_rand(this.split_percentage);
      }
      else if (this.mix_parameter == 3){
        random_cell = get_cell_new_rand(this.split_percentage);
      }
      else {
        random_cell = choose_rand_cell_in_list();
      }
      
      
      random_cell.highlight(10, 255, 10, 220);
      Cell neighbour = checkNeighbours(random_cell);
      cells_part_of_maze.add(random_cell);
      if (neighbour != null)
      {
        //highlight_neighbours(random_cell);
        neighbour.visited = true;
        list_of_cells.add(neighbour);
        cells_part_of_maze.add(neighbour);
        //neighbour.highlight(220, 20, 20, 220);
        //create a passage
        remove_walls(random_cell, neighbour);
        
      }
      else 
      {
        list_of_cells.remove(random_cell);    //remove the cell with no unvisited neighbours from the list
      }
    }
    else
    {
      return true;
    }
    return false;

  }
  
  void generate_maze()
  {
    /*
    1. Let C be a list of cells, initially empty. Add one cell to C, at random.
    2. Choose a cell from C, and carve a passage to any unvisited neighbor of that cell, 
    adding that neighbor to C as well. If there are no unvisited neighbors, remove the cell from C.
    3. Repeat #2 until C is empty.
    */    
    Cell current;
    //use random cell
    benchmark = System.currentTimeMillis();
    if (this.start_i < 0 || this.start_j < 0)
    {
      current = get_random_cell();
    }
    else
    {
      current = cell_object[this.start_i][this.start_j];
    }
    
    list_of_cells.add(current);    //add that cell to the list
    current.visited = true;
    while (list_of_cells.size() > 0)
    {
      Cell random_cell;
      if (this.mix_parameter == 1){
        random_cell = get_cell_old_new(this.split_percentage);
      }
      else if(this.mix_parameter == 2){
        random_cell = get_cell_old_rand(this.split_percentage);
      }
      else if (this.mix_parameter == 3){
        random_cell = get_cell_new_rand(this.split_percentage);
      }
      else {
        random_cell = choose_rand_cell_in_list();
      }
      
      Cell neighbour = checkNeighbours(random_cell);
      cells_part_of_maze.add(random_cell);
      if (neighbour != null)
      {
        neighbour.visited = true;
        list_of_cells.add(neighbour);
        cells_part_of_maze.add(neighbour);
        //create a passage
        remove_walls(random_cell, neighbour);
      }
      else 
      {
        list_of_cells.remove(random_cell);    //remove the cell with no unvisited neighbours from the list
      }
    }
    
    benchmark = System.currentTimeMillis() - benchmark;
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
  
  Cell get_random_cell()
  {
    int r_rows = floor(random(0, this.rows));
    int r_cols = floor(random(0, this.columns));
    return cell_object[r_rows][r_cols];
  }
  
  Cell choose_rand_cell_in_list()
  {
    if (list_of_cells.size() > 0)
    {
      int r = floor(random(0, list_of_cells.size()));
      return list_of_cells.get(r);
    }
    else
    {
      return null;
    }
  }
  
  Cell get_cell_old_rand(int percentage_random)
  {
    int rand_num = floor(random(0, 100));
    if (percentage_random > 99) percentage_random = 99;
    else if (percentage_random < 0) percentage_random = 0;
    if (list_of_cells.size() > 0)
    {
      //if the user wants 75% random number, the rand_num needs to be less than 75
      if (rand_num < percentage_random)
      {
        int r = floor(random(0, list_of_cells.size()));
        return list_of_cells.get(r);
      }
      else 
      {
        return list_of_cells.get(0);
      }
    }
    else    return null;
  }
  
  Cell get_cell_old_new(int percentage_new)
  {
    int rand_num = floor(random(0, 100));
    if (percentage_new > 99) percentage_new = 99;
    else if (percentage_new < 0) percentage_new = 0;
    if (list_of_cells.size() > 0)
    {
      //if the user wants 75% random number, the rand_num needs to be less than 75
      if (rand_num < percentage_new)
      {
        return list_of_cells.get(list_of_cells.size() - 1);
      }
      else 
      {
        return list_of_cells.get(0);
      }
    }
    else    return null;
  }
  
  Cell get_cell_new_rand(int percentage_random)
  {
    int rand_num = floor(random(0, 100));
    if (percentage_random > 99) percentage_random = 99;
    else if (percentage_random < 0) percentage_random = 0;
    if (list_of_cells.size() > 0)
    {
      //if the user wants 75% random number, the rand_num needs to be less than 75
      if (rand_num < percentage_random)
      {
        int r = floor(random(0, list_of_cells.size()));
        return list_of_cells.get(r);
      }
      else 
      {  //returns latest item in the array, basically a recursive back tracker 
        return list_of_cells.get(list_of_cells.size() - 1);
      }
    }
    else    return null;
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
  
  void setBackgroundColour(int r, int g, int b, int a)
  {
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      {  //create cell objects for every new square in the grid using rows and columns
        this.cell_object[i][j].setBackgroundColour(r, g, b, a);      //assigns column i and row j to a cell object
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
  void setOffset(int offset_x_in_pixels, int offset_y_in_pixels)
  {    
    this.off_x = offset_x_in_pixels;
    this.off_y = offset_y_in_pixels;
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      {  //create cell objects for every new square in the grid using rows and columns
        this.cell_object[i][j].setOffsets(offset_x_in_pixels, offset_y_in_pixels);      //assigns column i and row j to a cell object
      }
    }
  }
  void setStartCell(int i, int j)
  {
    if (i > columns - 1) i = columns  - 1;
    if (j > rows - 1) j = rows  - 1;
    this.start_i = i;
    this.start_j = j;
  }
  void show_cells_in_maze()
  {
    
    if (System.currentTimeMillis() - last_change >= 100)
    {
      last_change = System.currentTimeMillis();
      for (int c = 0; c < cells_part_of_maze.size(); c++)
      {
        cells_part_of_maze.get(c).background_rgba[0]+=1 ;
        cells_part_of_maze.get(c).background_rgba[1]+=1 ;
        cells_part_of_maze.get(c).background_rgba[2]+=1 ;
      }
    }
      
    
    for (int c = 0; c < cells_part_of_maze.size(); c++)
    {
      //Cell current = cells_part_of_maze.get(c);
      /*
      float ratio_i = float(current.i) / this.columns;
      float ratio_j = float(current.j) / this.rows;
      r = int(90 + (ratio_j * scale)); 
      g = int(70 + (ratio_i * scale)); 
      b = int(150 + (ratio_i * scale)); 
      System.out.println("b " + b);
      
      current.setBackgroundColour(r, g, b, current.background_rgba[3]);
      */
      cells_part_of_maze.get(c).drawBox();
      cells_part_of_maze.get(c).drawWalls();
      int i = cells_part_of_maze.get(c).i;
      int j = cells_part_of_maze.get(c).j;
      float x = (i * this.side) + this.side*0.15;
      float y = (j * this.side) +  this.side*0.5; 
      textSize(8);
      fill(255,255,255,255);
      //text(""+i + " " +j, x, y);
    }
  }
  void highlight_neighbours(Cell cell)
  {
    
    //check for out of bounds
    if (cell.j-1 >= 0)
    {
      if (cell_object[cell.i][cell.j-1].visited == false)
      {
        cell_object[cell.i][cell.j-1].highlight(200, 100, 20, 200);
      }
    }
    if (cell.i+1 <= columns -1) 
    {
      if (cell_object[cell.i+1][cell.j].visited == false)
      {
        cell_object[cell.i+1][cell.j].highlight(200, 100, 20, 200);
      }
    }
    if (cell.j +1 <= rows -1)
    {
      if (cell_object[cell.i][cell.j+1].visited == false)
      {
        cell_object[cell.i][cell.j+1].highlight(200, 100, 20, 200);
      }
    }
    if (cell.i -1 >= 0)
    {
      if (cell_object[cell.i-1][cell.j].visited == false)
      {
        cell_object[cell.i-1][cell.j].highlight(200, 100, 20, 200);
      }
    }
  }
  void draw_grid()
  {
    //grab the offset amount
    int offs_x = this.off_x;
    int offs_y = this.off_y;
    int maze_w = this.columns * this.side;
    stroke(255, 255, 255, 255);
    strokeWeight(1);
    for (int i =1; i <= this.columns; i++)
    {
      line(offs_x + (i * this.side), offs_y,     offs_x+(i * this.side),  offs_y + maze_w);
    }
    for (int j =1; j <= this.rows; j++)
    {
      line(offs_x, offs_y + (j * this.side),     offs_x+ maze_w, offs_y + (j * this.side));
    }
    strokeWeight(this.side * 0.25);
    //draws the outside edges with a thicker line, pleasing to the eye
    line(offs_x, offs_y,     offs_x, offs_y + maze_w);
    line(offs_x, offs_y,     offs_x + maze_w, offs_y);
    line(offs_x + (maze_w), offs_y,       offs_x + maze_w, offs_y + maze_w);
    line(offs_x, offs_y + maze_w,       offs_x+maze_w, offs_y + maze_w);
  }
  void setAllUndiscovered()
  {
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      { 
        this.cell_object[i][j].DFS_discovered = false;
      }
    }    
  }
}