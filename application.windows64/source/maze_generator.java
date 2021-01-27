import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Stack; 
import java.util.Random; 
import controlP5.*; 
import java.util.Random; 
import java.time.Duration; 
import java.time.Instant; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class maze_generator extends PApplet {





//GUI
ControlP5 cp5;
DropdownList d1;
DropdownList mix;
Slider slider_width;
Slider slider_side;
Slider slider_r;
Slider slider_g;
Slider slider_b;
Slider slider_wr;
Slider slider_wg;
Slider slider_wb;
Slider slider_fr;
Slider slider_fg;
Slider slider_fb;
Slider slider_split;

Textlabel start_lbl;
Textlabel end_lbl;

int maze_selected;
boolean new_maze = false;
boolean set_start_cell = false;
boolean build_maze = false;
int cell_i = -1;
int cell_j = -1;
int start_gen_i = -1;
int start_gen_j = -1;
int dfs_start_i = 0;
int dfs_start_j = 0;
int dfs_end_i = 0;
int dfs_end_j = 0;
boolean run_dfs = false;

int dfs_counter = 0;
int cells = 0;
boolean dfs_found = false;
//benchmarks
long benchmark;
Instant end; Instant start;
Instant k_end; Instant k_start;

ArrayList<Cell> solutionCells = new ArrayList<Cell>();
ArrayList<Cell> DFScells = new ArrayList<Cell>();

Growing_Tree            gmaze = new Growing_Tree();
Recursive_Backtracker   rmaze = new Recursive_Backtracker();
Kruskals_Maze           kmaze = new Kruskals_Maze();

  
//  ##################################################      SETUP      #######################################
public void setup()
{
  
  frameRate(120);
  //Cell root = kmaze.cell_object[0][0];
  //DFS(root, rmaze.cell_object[rmaze.columns - 1][rmaze.columns - 1]);
    
     
  cp5 = new ControlP5(this);
  Textlabel lbl1 = cp5.addTextlabel("label1")
                    .setText("Select a maze generation algorithm")
                    .setPosition(10,20);
                    
  slider_width = cp5.addSlider("Size of Maze",         100, 800, 600, 10, 60, 120, 20);
  slider_side = cp5.addSlider("Side length",           1, 50, 15, 10, 100, 120, 20);
  
  Textlabel lbl2 = cp5.addTextlabel("label2")
                    .setText("Select Background colour")
                    .setPosition(10, 135);
  slider_r = cp5.addSlider("\n\n R",           0, 255, 70, 10, 150, 40, 20);
  slider_g = cp5.addSlider("\n\n G",           0, 255, 25, 60, 150, 40, 20);
  slider_b = cp5.addSlider("\n\n B",           0, 255, 170, 110, 150, 40, 20);
  
  Textlabel lbl3 = cp5.addTextlabel("label3")
                    .setText("Select Gradient colour")
                    .setPosition(10, 190);
  slider_fr = cp5.addSlider("\n\n Gradient R",           0, 255, 75, 10, 200, 40, 20);
  slider_fg = cp5.addSlider("\n\n Gradient G",           0, 255, 170, 60, 200, 40, 20);
  slider_fb = cp5.addSlider("\n\n Gradient B",           0, 255, 200, 110, 200, 40, 20);
  
                    
  slider_wr = cp5.addSlider("\n\n Wall R",           0, 255, 10, 10, 250, 40, 20);
  slider_wg = cp5.addSlider("\n\n Wall G",           0, 255, 10, 60, 250, 40, 20);
  slider_wb = cp5.addSlider("\n\n Wall B",           0, 255, 10, 110, 250, 40, 20);
        
  Textlabel lbl4 = cp5.addTextlabel("label4")
                    .setText("Additional Options for Growing Tree Mazes")
                    .setPosition(10, 300);
                    
                    
  slider_split = cp5.addSlider("Split Percent",           0, 100, 5, 10, 340, 120, 20);
                    
  Textlabel lbl5 = cp5.addTextlabel("label5")
                    .setText("Depth First Search Solver")
                    .setPosition(10, 470);
                    
  start_lbl = cp5.addTextlabel("start_lbl")
                .setText("Start coords: 0, 0")
                .setPosition(10, 500);
  end_lbl = cp5.addTextlabel("end_lbl")
                .setText("End coords: 0, 0")
                .setPosition(10, 520);
 //************************************************       
 
  cp5.addButton("Set_Begin").setValue(0).setPosition(110, 490).setSize(80, 20);
  cp5.addButton("Set_End").setValue(0).setPosition(110, 515).setSize(80, 20);
  cp5.addButton("Run_DFS").setValue(0).setPosition(40, 550).setSize(120, 20);  
  
  cp5.addButton("Animate").setValue(0).setPosition(20, 380).setSize(60, 20);
  cp5.addButton("Stop").setValue(0).setPosition(100, 380).setSize(60, 20);
  cp5.addButton("Start_Cell").setValue(0).setPosition(20, 410).setSize(100, 30);
  cp5.addButton("Render").setValue(0).setPosition(20, 600).setSize(160, 60);
  
  d1 = cp5.addDropdownList("Select an algorithm").setPosition(10, 40).setSize(150, 500);
  d1.addItem("Growing Tree Algorithm", 0).setItemHeight(30);
  d1.addItem("Recursive Backtracker", 1).setItemHeight(30);
  d1.addItem("Kruskal's", 2).setItemHeight(30); 
  
  mix = cp5.addDropdownList("Select a mix type").setPosition(10, 320).setSize(150, 500);
  mix.addItem("Always Random", 0).setItemHeight(30);
  mix.addItem("Old to New Cell Mix", 1).setItemHeight(30);
  mix.addItem("Old to Random Mix", 2).setItemHeight(30);
  mix.addItem("New to Random Mix", 3).setItemHeight(30);
  
  
}

//  ################################################      DRAW       ########################################################
boolean init = false;
public void draw()
{
  background(60);
  strokeWeight(2);
  stroke(255, 255, 255, 255);
  line(200, 0, 200, height);
  line(0, 290, 200, 290);
  line(0, 580, 200, 580);
  stroke(255, 255, 255, 50);
  line(0, 460, 200, 460);
  maze_selected = PApplet.parseInt(d1.getValue());
  int r = PApplet.parseInt(slider_r.getValue());
  int g = PApplet.parseInt(slider_g.getValue());
  int b = PApplet.parseInt(slider_b.getValue());
  int fr = PApplet.parseInt(slider_fr.getValue());
  int fg = PApplet.parseInt(slider_fg.getValue());
  int fb = PApplet.parseInt(slider_fb.getValue());
  //tree
  if (maze_selected == 0)
  {
    if (new_maze)
    {
      dfs_counter = 0;
      solutionCells.clear();
      DFScells.clear();
      gmaze = null;
      rmaze = null;
      kmaze = null;  
      gmaze = new Growing_Tree();
      gmaze.begin(PApplet.parseInt(slider_width.getValue()), PApplet.parseInt(slider_side.getValue()));
      gmaze.setOffset(250, 50);
      gmaze.setWallColour(PApplet.parseInt(slider_wr.getValue()), PApplet.parseInt(slider_wg.getValue()), PApplet.parseInt(slider_wb.getValue()), 255);
      gmaze.setColourGradient(r, g, b, fr, fg, fb);
      gmaze.setMix(PApplet.parseInt( mix.getValue()));
      gmaze.setSplitPercentage(PApplet.parseInt(slider_split.getValue()));
      gmaze.setStartCell(start_gen_i, start_gen_j);
      gmaze.generate_maze();
      new_maze = false;
    }
    else if (build_maze)
    {
      if (init == false)
      {      
        dfs_counter = 0;
        solutionCells.clear();
        DFScells.clear();
        gmaze = null;
        rmaze = null;
        kmaze = null;
        gmaze = new Growing_Tree();
        gmaze.begin(PApplet.parseInt(slider_width.getValue()), PApplet.parseInt(slider_side.getValue()));
        gmaze.setOffset(250, 50);
        gmaze.setWallColour(PApplet.parseInt(slider_wr.getValue()), PApplet.parseInt(slider_wg.getValue()), PApplet.parseInt(slider_wb.getValue()), 255);
        gmaze.setColourGradient(r, g, b, fr, fg, fb);
        gmaze.setMix(PApplet.parseInt( mix.getValue()));
        gmaze.setSplitPercentage(PApplet.parseInt(slider_split.getValue()));
        gmaze.setStartCell(start_gen_i, start_gen_j);
        init = true;
      }
      if (gmaze.build_animation())
      {
        build_maze = false;
        init = false;
      }
    }
    else
    {
      if (run_dfs == true)
      {
        gmaze.setAllUndiscovered();
        dfs_counter = 0;
        cells = 0;
        dfs_found = false;
        solutionCells.clear();
        DFScells.clear();
        //runs the Depth first search algorithm
        if (dfs_end_i < gmaze.columns && dfs_end_j < gmaze.rows)
        {
          DFS(gmaze.cell_object[dfs_start_i][dfs_start_j], gmaze.cell_object[dfs_end_i][dfs_end_j]);
        }
        run_dfs = false;
      }
      gmaze.update();
      displayDFS();
    }
  }
  //recursive
  else if (maze_selected == 1)
  {
    if (new_maze)
    {
      gmaze = null;
      rmaze = null;
      kmaze = null;
      rmaze = new Recursive_Backtracker();
      rmaze.begin(PApplet.parseInt(slider_width.getValue()), PApplet.parseInt(slider_side.getValue()));
      rmaze.setOffset(250, 50);
      rmaze.setWallColour(PApplet.parseInt(slider_wr.getValue()), PApplet.parseInt(slider_wg.getValue()), PApplet.parseInt(slider_wb.getValue()), 255);
      rmaze.setColourGradient(r, g, b, fr, fg, fb);
      
      rmaze.generate_maze();
      new_maze = false;
    }
    if (rmaze != null)
    {
      rmaze.update();
    }
  }
  //kruskals
  else if (maze_selected == 2)
  {
    if (new_maze)
    {
      gmaze = null;
      rmaze = null;
      kmaze = null;
      kmaze = new Kruskals_Maze(); 
      kmaze.begin(PApplet.parseInt(slider_width.getValue()), PApplet.parseInt(slider_side.getValue()));
      kmaze.setOffset(250, 50);
      kmaze.setWallColour(PApplet.parseInt(slider_wr.getValue()), PApplet.parseInt(slider_wg.getValue()), PApplet.parseInt(slider_wb.getValue()), 255);
      kmaze.setColourGradient(r, g, b, fr, fg, fb);
      
      kmaze.generate_maze();
      new_maze = false;
    }
    if(kmaze != null)
    {
      kmaze.update();
    }
  }
}

public void mouseClicked() 
{
  //which cell was clicked on
  cell_i = (mouseX - 250)/PApplet.parseInt(slider_side.getValue());
  cell_j = (mouseY - 50)/PApplet.parseInt(slider_side.getValue());
  if (gmaze != null)
  {
    if (cell_j >= gmaze.rows || cell_i >= gmaze.columns)
    {
      cell_i = cell_j = -1;
    }
  }
  
  System.out.println("i = " +cell_i + " j = " + cell_j + " st " + set_start_cell);
  if (set_start_cell == true)
  {
    start_gen_i = cell_i;
    start_gen_j = cell_j;
    set_start_cell = false;
  }
}

public void Set_Begin(int value) {
  if (cell_i >= 0 && cell_j >= 0)
  {
    dfs_start_i = cell_i;
    dfs_start_j = cell_j;
    start_lbl.setText("Start coords: "+dfs_start_i + ", " + dfs_start_j);
  }
}

public void Set_End(int value) {
  if (cell_i >= 0 && cell_j >= 0)
  {
    dfs_end_i = cell_i;
    dfs_end_j = cell_j;
    end_lbl.setText("End coords: "+dfs_end_i + ", " + dfs_end_j);
  }
}

public void Run_DFS(int value) {
  if (frameCount > 60)
  {
    run_dfs = true;
  }
}

public void Render(int value) {
  if (frameCount > 60){
    new_maze = true;
  }
}
public void Animate(int value) {
  if (frameCount > 60){
    build_maze = true;
  }
  set_start_cell = false;
}
public void Stop(int value) {
  if (frameCount > 60){
    build_maze = false;
    init = false;
  }
  set_start_cell = false;
}
public void Start_Cell(int value) {
  if (frameCount > 60){
    start_gen_i = cell_i;
    start_gen_j = cell_j;
    set_start_cell = false;
  }
  System.out.println("prev " +start_gen_i + " prev j " + start_gen_j + " and " + set_start_cell);
}

public void controlEvent(ControlEvent event) 
{
}


public void displayDFS()
{
  if (dfs_counter < DFScells.size())dfs_counter++;
  for (int i = 0; i < dfs_counter; i++)
  {
    DFScells.get(i).highlight(10, 220, 30, 150);
    
    if (dfs_counter >= DFScells.size())
    {  
      for (int j = 0; j < solutionCells.size(); j++)
      {
        solutionCells.get( j).highlight(150, 20, 30, 30);
      }
    }
  }
}

public boolean DFS(Cell node, Cell end)
{
  
  node.DFS_discovered = true;
  //node.highlight(0,255,0, 200);
  cells++;
  //wall = {top, right, bottom, left}
  if (node.i == end.i && node.j == end.j)
  {
    System.out.println("Found end " + cells);
    solutionCells.add(node);
    dfs_found = true;
    return true;
  }
  if (dfs_found == false)
  {
    DFScells.add(node);
    //for all edges
    if (node.wall[1] == false && node.i+1 <= gmaze.columns -1) 
    {
      if (gmaze.cell_object[node.i+1][node.j].DFS_discovered == false)
      {
        DFS(gmaze.cell_object[node.i+1][node.j], end);
      }
    }
    if (node.wall[2] == false && node.j +1 <= gmaze.rows -1)
    {
      if (gmaze.cell_object[node.i][node.j+1].DFS_discovered == false)
      {
        DFS(gmaze.cell_object[node.i][node.j+1], end);
      }
    }
    if (node.wall[3] == false && node.i -1 >= 0)
    {
      if (gmaze.cell_object[node.i-1][node.j].DFS_discovered == false)
      {
        DFS(gmaze.cell_object[node.i-1][node.j], end);
      }
    }
    if (node.wall[0] == false && node.j-1 >= 0)
    {
      if (gmaze.cell_object[node.i][node.j-1].DFS_discovered == false)
      {
        DFS(gmaze.cell_object[node.i][node.j-1], end);
      }
    }
    if (dfs_found == true)   solutionCells.add(node);
    //if (discoveredNeighbour == false)   solutionCells.remove(node);
    
  }
  //else solutionCells.add(node);
  return false;
}



/*
void keyPressed() 
{
  int keyIndex = -1;
  if (key >= 'A' && key <= 'Z') {
    keyIndex = key - 'A';
  } else if (key >= 'a' && key <= 'z') {
    keyIndex = key - 'a';
  }
  if (keyIndex == -1) 
  {
    // If it's not a letter key, clear the screen
    //background(0);
  } 
  else { 
    // It's a letter key, fill a rectangle
    
  }
}
*/


//  ##################################################    CELL    ######################################################
//represents a square on a grid. 
class Cell
{
  public boolean DFS_discovered = false;
  public boolean part_of_maze = false;
  public int set_number;
  private int i;
  private int j;
  private int offset_x = 0;
  private int offset_y = 0;
  //initiates an array holding a boolean that knows if a wall exists on this cell or not
  //        wall = {top, right, bottom, left}
  public boolean[] wall = {true, true, true, true};
  public boolean visited = false;
  private int side;
  public int[] background_rgba = {15, 15 , 230, 100};    //defualt values
  public int[] wall_rgba = {10, 10 , 10, 255};    //defualt values
  
  //constructor
  Cell(int i_coord, int j_coord, int side) 
  {
    this.i = i_coord;
    this.j = j_coord;
    this.visited = false;
    this.side = side;
  }
  public void setOffsets(int offset_x_in_pixels, int offset_y_in_pixels)
  {
    this.offset_x = offset_x_in_pixels;
    this.offset_y = offset_y_in_pixels;
  }
  public void setBackgroundColour(int r, int g, int b, int a)
  {
    this.background_rgba[0] = r;
    this.background_rgba[1] = g;
    this.background_rgba[2] = b;
    this.background_rgba[3] = a;
  }
  public void setWallColour(int r, int g, int b, int a)
  {  
    this.wall_rgba[0] = r;
    this.wall_rgba[1] = g;
    this.wall_rgba[2] = b;
    this.wall_rgba[3] = a;
  }
  public void drawBox()      //displays
  {    //calculates the x and y coordinates using its position i,j
    int x = this.i * this.side + this.offset_x;
    int y = this.j * this.side + this.offset_y;
    //if a wall exists on this cell, draw it
    
    if (this.visited)    //if a cell is visited, make it visible on the grid
    {
      noStroke();
      fill(this.background_rgba[0], this.background_rgba[1] , this.background_rgba[2], this.background_rgba[3]);    //purple
      rect(x, y, side, side, 3);     
    }
  }
  public void drawWalls()
  {
    stroke(this.wall_rgba[0], this.wall_rgba[1] , this.wall_rgba[2], this.wall_rgba[3]);
    strokeJoin(MITER);
    strokeCap(PROJECT);
    strokeWeight(this.side*0.15f);
    int x = this.i * this.side + this.offset_x;
    int y = this.j * this.side + this.offset_y;
    if (this.wall[0])
    {
      line(x, y, x + this.side, y);                //top
    }
    if (this.wall[1])
    {
      line(x+this.side, y, x + this.side, y + side);    //right
    }
    if (this.wall[2])
    {
      line(x, y +this.side, x + this.side, y + this.side);  //bottom
    }
    if (this.wall[3])
    {
      line(x, y, x, y+this.side);                //left
    }
  }
  
  public void highlight(int r, int g, int b, int a)    //highlights the cell
  {
    int x = this.i * this.side + this.offset_x;
    int y = this.j * this.side + this.offset_y;
    noStroke();
    fill(r, g, b, a);
    int c = PApplet.parseInt(this.side*0.1f);
    rect(x+c, y+c, side -c*2, this.side-c*2);
    
   
    
  }
  
}

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
  public void begin(int maze_width, int side_size)
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
        float ratio_i = PApplet.parseFloat(current.i) / this.columns;
        float ratio_j = PApplet.parseFloat(current.j) / this.rows;
        int r = PApplet.parseInt(50 + (ratio_j * 80)); 
        int g = PApplet.parseInt(70 + (ratio_i * 90)); 
        int b = PApplet.parseInt(150 + (ratio_i * 100)); 
        this.cell_object[i][j].setBackgroundColour(r, g, b, 255);
      }
    }
    this.cell_object[0][0].wall[3] = false;
    this.cell_object[this.columns-1][this.rows-1].wall[1] = false;
  }
  
  public void setColourGradient(int start_r, int start_g, int start_b, int factor_r, int factor_g, int factor_b)
  {
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      { 
        Cell current = this.cell_object[i][j];
        float ratio_i = PApplet.parseFloat(current.i) / this.columns;
        float ratio_j = PApplet.parseFloat(current.j) / this.rows;
        int r = PApplet.parseInt(start_r + (ratio_j * factor_r)); 
        int g = PApplet.parseInt(start_g + (ratio_i * factor_g)); 
        int b = PApplet.parseInt(start_b + (ratio_i * factor_b)); 
        this.cell_object[i][j].setBackgroundColour(r, g, b, 255);
      }
    }
  }
  
  public void setMix(int setting)
  {
    if (setting > 3)         setting = 0;
    else if (setting < 0)    setting = 3;
    this.mix_parameter = setting;
  }
  public void setSplitPercentage(int percentage)
  {
    if (percentage > 100)         percentage = 100;
    else if (percentage < 0)      percentage = 0;
    this.split_percentage =       percentage;
  }
  
  public boolean build_animation()
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
  
  public void generate_maze()
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
  
  public void update()
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
  
  public Cell get_random_cell()
  {
    int r_rows = floor(random(0, this.rows));
    int r_cols = floor(random(0, this.columns));
    return cell_object[r_rows][r_cols];
  }
  
  public Cell choose_rand_cell_in_list()
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
  
  public Cell get_cell_old_rand(int percentage_random)
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
  
  public Cell get_cell_old_new(int percentage_new)
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
  
  public Cell get_cell_new_rand(int percentage_random)
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
  
  public void remove_walls(Cell cell, Cell neighbour)
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
  
  public Cell checkNeighbours(Cell cell)
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
  
  public void setBackgroundColour(int r, int g, int b, int a)
  {
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      {  //create cell objects for every new square in the grid using rows and columns
        this.cell_object[i][j].setBackgroundColour(r, g, b, a);      //assigns column i and row j to a cell object
      }
    }
  }
  public void setWallColour(int r, int g, int b, int a)
  {
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      {  //create cell objects for every new square in the grid using rows and columns
        this.cell_object[i][j].setWallColour(r, g, b, a);      //assigns column i and row j to a cell object
      }
    }
  }
  public void setOffset(int offset_x_in_pixels, int offset_y_in_pixels)
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
  public void setStartCell(int i, int j)
  {
    if (i > columns - 1) i = columns  - 1;
    if (j > rows - 1) j = rows  - 1;
    this.start_i = i;
    this.start_j = j;
  }
  public void show_cells_in_maze()
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
      float x = (i * this.side) + this.side*0.15f;
      float y = (j * this.side) +  this.side*0.5f; 
      textSize(8);
      fill(255,255,255,255);
      //text(""+i + " " +j, x, y);
    }
  }
  public void highlight_neighbours(Cell cell)
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
  public void draw_grid()
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
    strokeWeight(this.side * 0.25f);
    //draws the outside edges with a thicker line, pleasing to the eye
    line(offs_x, offs_y,     offs_x, offs_y + maze_w);
    line(offs_x, offs_y,     offs_x + maze_w, offs_y);
    line(offs_x + (maze_w), offs_y,       offs_x + maze_w, offs_y + maze_w);
    line(offs_x, offs_y + maze_w,       offs_x+maze_w, offs_y + maze_w);
  }
  public void setAllUndiscovered()
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
  public void begin(int maze_width, int square_size)
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
  
  public void setOffset(int offset_x_in_pixels, int offset_y_in_pixels)
  {    
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      {  //create cell objects for every new square in the grid using rows and columns
        this.cell_object[i][j].setOffsets(offset_x_in_pixels, offset_y_in_pixels);      //assigns column i and row j to a cell object
      }
    }
  }
  public void setWallColour(int r, int g, int b, int a)
  {
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      {  //create cell objects for every new square in the grid using rows and columns
        this.cell_object[i][j].setWallColour(r, g, b, a);      //assigns column i and row j to a cell object
      }
    }
  }
  
  public void setColourGradient(int start_r, int start_g, int start_b, int factor_r, int factor_g, int factor_b)
  {
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      { 
        Cell current = this.cell_object[i][j];
        float ratio_i = PApplet.parseFloat(current.i) / this.columns;
        float ratio_j = PApplet.parseFloat(current.j) / this.rows;
        int r = PApplet.parseInt(start_r + (ratio_j * factor_r)); 
        int g = PApplet.parseInt(start_g + (ratio_i * factor_g)); 
        int b = PApplet.parseInt(start_b + (ratio_i * factor_b)); 
        this.cell_object[i][j].setBackgroundColour(r, g, b, 255);
      }
    }
  }
  
  public void remove_walls(Cell cell, Cell neighbour)
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
  public Cell checkNeighbours(Cell cell)
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
  public void generate_maze()
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
  public void update()
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
  
  public void begin(int maze_width, int square_size)
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
  public void generate_maze()
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
  
  public void setOffset(int offset_x_in_pixels, int offset_y_in_pixels)
  {    
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      {  //create cell objects for every new square in the grid using rows and columns
        this.cell_object[i][j].setOffsets(offset_x_in_pixels, offset_y_in_pixels);      //assigns column i and row j to a cell object
      }
    }
  }
  
  public Cell get_random_neighbour(Cell cell)
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
  
  public void replace_all_cells_with_matching_set(int set_to_find, int set_to_swap)
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
  
  public void remove_walls(Cell cell, Cell neighbour)
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
  public void update()
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
  
  public void setWallColour(int r, int g, int b, int a)
  {
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      {  //create cell objects for every new square in the grid using rows and columns
        this.cell_object[i][j].setWallColour(r, g, b, a);      //assigns column i and row j to a cell object
      }
    }
  }
  
  public void setColourGradient(int start_r, int start_g, int start_b, int factor_r, int factor_g, int factor_b)
  {
    for (int j=0; j < this.rows; j++)
    {
      for (int i=0; i < this.columns; i++)
      { 
        Cell current = this.cell_object[i][j];
        float ratio_i = PApplet.parseFloat(current.i) / this.columns;
        float ratio_j = PApplet.parseFloat(current.j) / this.rows;
        int r = PApplet.parseInt(start_r + (ratio_j * factor_r)); 
        int g = PApplet.parseInt(start_g + (ratio_i * factor_g)); 
        int b = PApplet.parseInt(start_b + (ratio_i * factor_b)); 
        this.cell_object[i][j].setBackgroundColour(r, g, b, 255);
      }
    }
  }
  
}
  public void settings() {  size(1100, 900); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "maze_generator" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
