###########################################################
    #  Computer Project #7
    #
    #  Loop
    #    prompt for user input
    #       Check what command was entered
    #       Calls correct functions based on what was enetered
    #       call function to count number of digits in integer
    #       Formats output
    ###########################################################

import sys

def process_file():
    """
        Turns file into dictionary
 
        Receive:  None
        Return:   Dictionary
        Algorithm:
           Loops through each line
               Splits up each line
               First part of dictionary is set to first split
               Each additional split is linked to each name
               Does calculations to for needed information and adds to dictionary
           return Dictionary
    """

    player_dict = {}
    #Loops through each line
    for line in mlb_file.readlines():
        #Finds splits based on ; 
        split_str = line.split(';')
        #Sets first part to the name
        name_str = split_str[0]
        #Strips white space out of splits
        data_tup = tuple(x.strip() for x in split_str[1:])
        #Sets the everything but the name to a tuple
        data_tup = (data_tup[0],int(data_tup[1]),int(data_tup[2]),int(data_tup[3]),
                     int(data_tup[4]),int(data_tup[5]),int(data_tup[6]),int(data_tup[7]))
        #Calculations for singles, total bases, batting avg, and slug per
        singles = data_tup[4]-(data_tup[5]+data_tup[6]+data_tup[7])
        total_bases = singles + 2*(data_tup[5])+3*(data_tup[6])+4*(data_tup[7])
        batting_avg = (data_tup[4])/(data_tup[2])
        slug_per = (total_bases)/(data_tup[2])
        #New tuple with calculations
        data2_tup = (data_tup[0],data_tup[1],data_tup[2],data_tup[3],
                     data_tup[4],data_tup[5],data_tup[6],data_tup[7],
                     round(batting_avg,3),round(slug_per,3))
        #Sets elements to dictionary        
        player_dict[name_str] = data2_tup
    #Returns dictionary
    return player_dict

def command_str():
    #Simple function that prints commands
    print("Commands:")
    print("quit")
    print("help")
    print("input filename")
    print("team identifier")
    print("report N hits")
    print("report N batting")
    print("report N slugging")

def open_file():
    """
        Opens file
 
        Receive:  None
        Return:   File
        Algorithm:
               Gets file name from user input after 6th character
               Tries to open file, prompts the user twice more if invalid name is enetered
                   Else, program halts
           return File
    """
    count = 0
    #Gets user input 
    file_name = user_str[6:]
    #Loops while count is less than 2
    while count < 2:
        #Tries to open file and returns file
        try:
            mlb_file = open(file_name,"r")
            return mlb_file
        except IOError:
            #If file does not exist, prompts for new file
            print("File not found!")
            file_name = input("Enter new file name: ")
        count += 1
    #Prints closing message and ends program        
    print("Halting program, no valid file name entered.")
    sys.exit()
    

def report_file(user_str):
    """
        Gets names for report
 
        Receive:  User input
        Return:   List of names
        Algorithm:
               Loops through each name
                Splits user string
                Try to set splits catches errors if it does not exist
                Returns names based on what report user wants
                
          
    """
    #Splits user input
    split_str = user_str.split(' ')
    #Tries to set each split, if does not exist message to user saying to enter correct report type and integer
    try:
        top_str = split_str[1]
        cat_str = split_str[2]
        top_names = []
        #Checks if second split is a digit
        if not top_str.isdigit():
            print("Please enter a valid number for n.")
        else:
            top_int = int(top_str)
        #Checks what 3rd split is, sorts dictionary based on what is entered and grabs top names
        #Sorts first by top catagory, then alphabetically
        if cat_str.upper() == "HITS":
            sorted_x = sorted(player_dict.items(), key=lambda x: (x[1][4],x[1][0]), reverse=True)
            list_names = [x[0] for x in sorted_x]
            top_names = list_names[0:top_int]

            
        elif cat_str.upper() == "BATTING":
            sorted_x = sorted(player_dict.items(), key=lambda x: (x[1][8],x[1][0]), reverse=True)
            list_names = [x[0] for x in sorted_x]
            top_names = list_names[0:top_int]
        
        elif cat_str.upper() == "SLUGGING":
            sorted_x = sorted(player_dict.items(), key=lambda x: (x[1][9],x[1][0]), reverse=True)
            list_names = [x[0] for x in sorted_x]
            top_names = list_names[0:top_int]
        
        else:
            print("Please enter a valid report type to report.")
        #Returns top names
        return top_names
    except IndexError:
        print("Please enter a proper integer and string after report")

def format_names(top_names, player_dict):
    """
        Formats names for readability and prints
 
        Receive:  List of names and dictionary
        Return:   None (Prints)
        Algorithm:
               Loops through each name
                Checks for longest name to set length of first column
                Prints headers
                Prints each name with dictionary elements set to certain spaces
          
    """
    name_len = 0
    #Loops through each name loooking for longest for spacing later on
    for x in range(0, len(top_names)):
        if name_len < len(top_names[x]):
            name_len = len(top_names[x]) + 5
    #Prints headers correctly spaced        
    print("Name".ljust(name_len) + "Team".ljust(6) + "Games Played".ljust(14) +
          "At Bats".ljust(9) + "Runs scored".ljust(13) + "Hits".ljust(6) + "Doubles".ljust(9) +
          "Triples".ljust(9) + "Homeruns".ljust(10) + "Batting Avg".ljust(13) + "Slugging Pct".ljust(14))
    #Loops through each name and prints it with its dictionary elements correctly spaced
    for x in range(0, len(top_names)):
        print(top_names[x].ljust(name_len) + str(player_dict[top_names[x]][0]).ljust(6) + str(player_dict[top_names[x]][1]).ljust(14) +
              str(player_dict[top_names[x]][2]).ljust(9) + str(player_dict[top_names[x]][3]).ljust(13) + str(player_dict[top_names[x]][4]).ljust(6) +
              str(player_dict[top_names[x]][5]).ljust(9) + str(player_dict[top_names[x]][6]).ljust(9) + str(player_dict[top_names[x]][7]).ljust(10) +
              str(player_dict[top_names[x]][8]).ljust(13) + str(player_dict[top_names[x]][9]).ljust(13))        
    
 #Calls function that displays commands   
command_str()
#Loops indefintetly
while True:
    #Asks user for command
    user_str = input("Command: ")
    #Checks what the user types in
        #If quit, ends loop
        #If input, calls open_file function
        #If report, calls process file function, resets the file back to first line, and calls the format function
    
    if user_str.upper() == "QUIT":
        break
    elif user_str.upper() == "HELP":
        command_str()
    elif user_str[0:5].upper() == "INPUT":
        mlb_file = open_file()
    elif user_str[0:6].upper() == "REPORT":
        #Try catch statement to check if a file was entered or not then calls functions
        try:
            player_dict = process_file()
            mlb_file.seek(0)
            top_names = report_file(user_str)
            format_names(top_names, player_dict)
        except FileNotFoundError:
            print("Please choose a valid input file first.")
        except NameError:
            print("Please choose a valid input file first.")
        

        #If team is entered, calls process_file function
        #Gets the team str, resets the file back to first line,
        #Checks if player is on the team and adds it to the list of names
        #Checks if list is empty
        #If not, sorts list of names
    elif user_str[0:4].upper() == "TEAM":
        player_dict = process_file()
        list_names = []
        team_str = user_str[5:]
        mlb_file.seek(0)
        for name in player_dict.keys():
            if player_dict[name][0] == team_str:
                list_names.append(name)
        if list_names == []:
            print("Please enter a valid team string.")
        else:
            print(sorted(list_names))
        #Reprints commands if invalid command is entered
    else:
        print("Please enter a valid command, here is the list.")
        command_str()
