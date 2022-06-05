import time
import sys 
import os 

from selenium import webdriver 
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains

import warnings
warnings.filterwarnings('ignore')

# 검색어 입력 
# guery = input('검색지역? ')
query = '광진구 일출'

driver = webdriver.Chrome("./chromedriver")
driver.get(f"https://map.naver.com/v5/search/{query}?c=14203933.7141038,4562681.4505997,10,0,0,0,dh")

driver.switch_to.frame("searchIframe")

title_list = []


try: 
    for i in range(1,2): 
        driver.find_element_by_link_text(str(i)).click()
        try: 
            for j in range(3,70,3):
                element = driver.find_elements_by_css_selector('._3Apve')[j]
                ActionChains(driver).move_to_element(element).key_down(Keys.PAGE_DOWN).key_up(Keys.PAGE_DOWN).perform()
        except:
            pass

        title_raw = driver.find_elements_by_css_selector("._3Apve")
        for title in title_raw:
            title = title.text
            title_list.append(title)
       
        
except:
    pass

print(len(title_list))
print(title_list)