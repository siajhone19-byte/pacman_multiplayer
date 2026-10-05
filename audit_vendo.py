import requests
from urllib.parse import urljoin

BASE_URL = "http://10.0.0.1"

session = requests.Session()

def check_page(path):
    url = urljoin(BASE_URL, path)

    try:
        r = session.get(url, timeout=5, allow_redirects=False)

        print(f"\n[{r.status_code}] {url}")
        print(f"Content-Type: {r.headers.get('Content-Type')}")
        print(f"Location: {r.headers.get('Location')}")
        print(f"Set-Cookie: {r.headers.get('Set-Cookie')}")

        return r
    except requests.RequestException as e:
        print(f"ERROR: {e}")
        return None


print("=== VENDO SECURITY AUDIT ===")
print(f"Target: {BASE_URL}")

home = check_page("/home")
login = check_page("/login")
status = check_page("/status")
payment = check_page("/payment")

print("\n=== SECURITY HEADERS ===")

if home:
    headers = [
        "X-Frame-Options",
        "X-Content-Type-Options",
        "Content-Security-Policy",
        "Strict-Transport-Security",
        "Referrer-Policy"
    ]

    for header in headers:
        value = home.headers.get(header)

        if value:
            print(f"[+] {header}: {value}")
        else:
            print(f"[-] Missing: {header}")

print("\n=== COOKIES ===")

for cookie in session.cookies:
    print(
        f"{cookie.name} | "
        f"Secure={cookie.secure} | "
        f"HttpOnly={cookie.has_nonstandard_attr('HttpOnly')}"
    )

print("\nAudit complete.")