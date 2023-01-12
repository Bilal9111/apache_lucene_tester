import json
import os
from googletrans import Translator

translator = Translator(service_urls=['translate.googleapis.com'])
c_dict = set()

with open("./master.txt") as file:
    count = 1
    for line in file:
        line_s = line.split("::")
        
        with open('docs_ar/'+str(count)+'.txt', 'w',encoding="utf-8") as f:
            
            line = translator.translate(line_s[2], dest="ar")
            if line.text in c_dict: continue
            c_dict.add(line.text)
            f.write(line.text)
            count += 1
