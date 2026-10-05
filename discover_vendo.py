import requests
from bs4 import BeautifulSoup
from urllib.parse import urljoin

BASE_URL = "http://10.0.0.1"

session = requests.Session()

r = session.get(BASE_URL + "/home", timeout=5)

print("Status:", r.status_code)
print("URL:", r.url)

soup = BeautifulSoup(r.text, "html.parser")

print("\n=== LINKS ===")

for a in soup.find_all("a", href=True):
    print(urljoin(r.url, a["href"]))

print("\n=== FORMS ===")

for form in soup.find_all("form"):
    print("Action:", urljoin(r.url, form.get("action", "")))
    print("Method:", form.get("method", "GET").upper())

    for inp in form.find_all(["input", "select", "button"]):
        print(
            " ",
            inp.name,
            "name=",
            inp.get("name"),
            "type=",
            inp.get("type"),
            "value=",
            inp.get("value")
        )

print("\n=== JAVASCRIPT ===")

for script in soup.find_all("script", src=True):
    print(urljoin(r.url, script["src"]))
