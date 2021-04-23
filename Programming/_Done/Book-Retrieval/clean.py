import pandas as pd
import os

def narrow(): # Narrows down the file columns
    fileList = os.listdir("2/")
    for curFile in fileList:
        f=pd.read_csv("2/" + curFile)
        # PagesNumber can be upper or lower case
        keep_col = ['Name','Publisher', 'Language','Authors','Rating','ISBN']
        # Clean of pagesNumber named PagesNumber
        changesNeeded = False
        try:
            print(f['pagesNumber'])
            keep_col.append('pagesNumber')
        except:
            changesNeeded = True
#            keep_col.append('PagesNumber')

        new_f = f[keep_col]
        if(changesNeeded):
            new_f['pagesNumber'] = f['PagesNumber']
            try:
                f.drop('PagesNumber')
            except:
                print("It seems I cannot drop it. I will just ignore it")
        # Adding PublishMonth, PublishDay, PublishYear
        new_f['PublishDate'] = f.PublishDay.astype(str) + '-' + f.PublishMonth.astype(str) + '-' + f.PublishYear.astype(str)
#        new_f.assign(PublishDate = f.PublishMonth.astype(str) + '-' + f.PublishDay.astype(str) + '-' + f.PublishYear.astype(str))
        
        if not os.path.exists("older"):
            os.mkdir("older")

        # Dropping books with 0 as rating
        a = (new_f["Rating"] == 0).tolist()
        new_f = new_f.drop(new_f.index[a])
        b = (new_f["Authors"] == "NOT A BOOK").tolist()
        new_f = new_f.drop(new_f.index[b])
#        c = a+b
#        new_f.index[new_f.index[new_f["Rating"] == 0].tolist()])
        # Dropping Non Books
#        new_f = new_f.drop(new_f.index[new_f.index[new_f["Authors"] == "NOT A BOOK"].tolist()])

#        print(new_f.head())
        os.rename("2/"+ curFile, "older/"  + curFile)
        new_f.to_csv("2/" + curFile, index=False)
        print("Cleaned " + curFile)
    
def combs(): # Combines csv files
    fileList = os.listdir("2/")
    li = []

    for filename in fileList:
        df = pd.read_csv("2/" + filename, index_col=None, header=0)
        li.append(df)

    frame = pd.concat(li, axis=0, ignore_index=True)
    df = frame.to_csv("allFiles.csv", index=False)
    
def main():
    narrow()
    combs()

main()
