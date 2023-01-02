with open("./master.txt") as file:
    count = 1
    for line in file:
        
        with open('docs/'+str(count)+'.txt', 'w') as f:
            line_s = line.split("::")
            for elem in line_s:
                f.write(elem + '\n')
            count += 1
