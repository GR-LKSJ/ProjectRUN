import time
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from time import sleep
from bs4 import BeautifulSoup
import re
import json

url = 'https://map.naver.com/v5/search'
driver = webdriver.Chrome('./chromedriver')  # 드라이버 경로
# driver = webdriver.Chrome('./chromedriver',chrome_options=options) # 크롬창 숨기기
driver.get(url)
key_word = '어린이대공원'  # 검색어, 바꿀 것

# css 찾을때 까지 10초대기
def time_wait(num, code):
    try:
        wait = WebDriverWait(driver, num).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, code)))
    except:
        print(code, '태그를 찾지 못하였습니다.')
        driver.quit()
    return wait


# css를 찾을때 까지 10초 대기
time_wait(10, 'div.input_box > input.input_search')

# 검색창 찾기
search = driver.find_element_by_css_selector('div.input_box > input.input_search')
search.send_keys(key_word)  # 검색어 입력
search.send_keys(Keys.ENTER)  # 엔터버튼 누르기

res = driver.page_source  # 페이지 소스 가져오기
soup = BeautifulSoup(res, 'html.parser')  # html 파싱하여  가져온다

sleep(1)


# frame 변경 메소드
def switch_frame(frame):
    driver.switch_to.default_content()  # frame 초기화
    driver.switch_to.frame(frame)  # frame 변경
    res
    soup


# 페이지 다운
def page_down(num):
    body = driver.find_element_by_css_selector('body')
    body.click()
    for i in range(num):
        body.send_keys(Keys.PAGE_DOWN)


# frame 변경
switch_frame('searchIframe')
page_down(40)
sleep(5)

# 매장 리스트
store_list = driver.find_elements_by_css_selector('._1EKsQ')
# 페이지 리스트
next_btn = driver.find_elements_by_css_selector('._2ky45 > a')

# dictionary 생성
store_dict = {'매장정보': []}
# 시작시간
start = time.time()
print('[크롤링 시작...]')

# 크롤링 (페이지 리스트 만큼)
for btn in range(len(next_btn))[1:]:  # next_btn[0] = 이전 페이지 버튼 무시 -> [1]부터 시작
    store_list
    for data in range(len(store_list)):  # 매장 리스트 만큼
        page = driver.find_elements_by_css_selector('.OXiLu')
        page[data].click()
        sleep(2)
        try:
            # 상세 페이지로 이동
            switch_frame('entryIframe')
            time_wait(5, '._3XamX')
            # 스크롤을 맨밑으로 1초간격으로 내린다.
            for down in range(3):
                sleep(1)
                driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")

            try:
                keyword_list = driver.find_element_by_css_selector('._28hFN')  # 키워드가 담긴 리스트 클릭
                keyword_list.click()

            except:  # 키워드리뷰 없으면 다음 음식점으로
                print('키워드리뷰 없음 >>> 다음으로',)
                switch_frame('searchIframe')
                continue

            try:
                keyword_review_list = driver.find_elements_by_css_selector('._3FaRE')  # 리뷰 리스트
                kwd_title = []
                kwd_count = []
                sleep(2)

                for i in keyword_review_list:
                    keyword_title = i.find_element_by_css_selector('._1lntw').text  # 키워드리뷰
                    keyword_count = i.find_element_by_css_selector('.Nqp-s').text   # 리뷰를 선택한 수

                    # db에 넣을 때 편의를 위해 요청하였음
                    title_re = re.sub('"', '', keyword_title) \
                        .replace('양이 많아요', '1').replace('음식이 맛있어요', '2').replace('재료가 신선해요', '3') \
                        .replace('가성비가 좋아요', '4').replace('특별한 메뉴가 있어요', '5').replace('화장실이 깨끗해요', '6') \
                        .replace('주차하기 편해요', '7').replace('친절해요', '8').replace('특별한 날 가기 좋아요', '9').replace(
                        '매장이 청결해요',
                        '10') \
                        .replace('인테리어가 멋져요', '11').replace('단체모임 하기 좋아요', '12').replace('뷰가 좋아요', '13').replace(
                        '매장이 넓어요',
                        '14') \
                        .replace('혼밥하기 좋아요', '15')

                    title_num = list(map(str, range(1, 16)))  # 1~15만 리스트에추가 (이외에 다른 키워드들은 추가하지않음)
                    count_keyword = re.sub('이 키워드를 선택한 인원\n', '', keyword_count)
                    if title_re in title_num:
                        kwd_title.append(title_re)
                        kwd_count.append(count_keyword)
                    else:
                        pass
            except:
                pass
            kwd_count = list(map(int, kwd_count))  # int 형변환

            print(kwd_title)
            print(kwd_count)

        except:
            print('ERROR!' * 3)

    # 다음 페이지 버튼
    if page[-1]:  # 마지막 매장일 경우 다음버튼 클릭
        next_btn[-1].click()
        sleep(2)
    else:
        print('페이지 인식 못함')
        break

print('[데이터 수집 완료]\n소요 시간 :', time.time() - start)
driver.quit()  # 작업이 끝나면 창을닫는다.

# json 파일로 저장
with open('data/store_data.json', 'w', encoding='utf-8') as f:
    json.dump(store_dict, f, indent=4, ensure_ascii=False)
            