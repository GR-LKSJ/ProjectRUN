#Step 0. 필요한 모듈과 라이브러리를 로딩하고 검색어를 입력 받습니다

from bs4 import BeautifulSoup
from selenium import webdriver
import time
import sys


query_txt = input('크롤링할 키워드는 무엇입니까?: ')
f_name = input('검색 결과를 저장할 파일경로와 이름을 지정하세요(예:c:\\data\\test.txt): ')
f_name = input('검색 결과를 저장할 txt 파일경로와 이름을 지정하세요(예:c:\\temp\\test.txt): ')
fc_name = input('검색 결과를 저장할 csv 파일경로와 이름을 지정하세요(예:c:\\temp\\test.csv): ')
fx_name = input('검색 결과를 저장할 xls 파일경로와 이름을 지정하세요(예:c:\\temp\\test.xls): ')
#크롤링할 키워드는 무엇입니까?: 어린이대공원
#검색 결과를 저장할 txt 파일경로와 이름을 지정하세요(예:c:\temp\test.txt): c:\data\test1.txt
#검색 결과를 저장할 csv 파일경로와 이름을 지정하세요(예:c:\temp\test.csv): c:\data\test1.csv
#검색 결과를 저장할 xls 파일경로와 이름을 지정하세요(예:c:\temp\test.xls): c:\data\test1.xls

#\t 메타 문자 조심
#f_name = 'C:\Temp\\textF_12.txt'
#fc_name = 'C:\Temp\\textFc_12.csv'
#fx_name = 'C:\Temp\\testFx_12.xlsx'

#Step 1. 크롬 드라이버를 사용해서 웹 브라우저를 실행합니다.
path = "c:/temp/chromedriver_240/chromedriver.exe"
driver = webdriver.Chrome(path)

driver.get("https://korean.visitkorea.or.kr/main/main.html")
time.sleep(2)  
# 창이 모두 열릴 때 까지 2초 기다립니다.

#Step 2. 검색창의 이름을 찾아서 검색어를 입력합니다
#검색창
element = driver.find_element_by_id("inp_search")
#팝업 버튼 닫기 클릭 
driver.find_element_by_css_selector("input#chkForm01").click()
#입력받은 글자 검색창에 입력
element.send_keys(query_txt)

#Step 3. 검색 버튼 클릭
driver.find_element_by_class_name("btn_search").click()

# Step 4. 현재 페이지에 있는 내용을 화면에 출력하기
time.sleep(1)

html = driver.page_source
soup = BeautifulSoup(html, 'html.parser')
content_list = soup.find('ul',class_='list_thumType type1')
print(content_list)

#데이터 내용에서 주요 부분 골라내기
no = 1
no2 = []
contents2 = []
tags2 = []

for i in content_list:
    no2.append(no)
    print('번호',no)

    try :
        tag = i.find('p','tag_type').get_text()
        tags2.append(tag)
        print('태그:',tag.strip())
        print("\n")
        
        no += 1
    except :
        tags2.append("태그 없음")
        print('태그: 태그없음')
        print("\n")
        no += 1