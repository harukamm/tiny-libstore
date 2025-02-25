# Description: This script is for manual testing.
# Run `DELETE from BOOK; DELETE from AUTHOR;` at psql before running this script.

import requests
import logging

HOST = "http://localhost:8080"
LOG_LEVEL = logging.DEBUG

def make_request(method, path, **kwargs):
    return requests.request(method, f'{HOST}{path}', **kwargs)

def api_call(method, path, bodystr=None):
    return make_request(method, path, data=bodystr, headers={'content-type': 'application/json'})

CALLS = [
  {
    'name': 'not found book',
    'args': ('GET', '/lib-store/v1.0/books/100'),
    'expected': (404, [])
  },
  {
    'name': 'with wrong path & method',
    'args': ('POST', '/lib-store/v1.0/books/100'),
    'expected': (405, ['not supported'])
  },
  {
    'name': 'invalid type in path variable',
    'args': ('PUT', '/lib-store/v1.0/books/undefined'),
    'expected': (400, ['Expected type int'])
  },
  {
    'name': 'create author A (id=500)',
    'args': ('POST', '/lib-store/v1.0/authors', '{ "id": 500, "name": "A-san", "birthDay": "1991-01-01" }'),
    'expected': (200, ['500'])
  },
  {
    'name': 'create author B (id=501)',
    'args': ('POST', '/lib-store/v1.0/authors', '{ "id": 501, "name": "B-san", "birthDay": "1992-02-02" }'),
    'expected': (200, ['501'])
  },
  {
    'name': 'create book (id=100)',
    'args': ('POST', '/lib-store/v1.0/books', '{ "id": 100, "title": "book1", "price": 999, "publishedStatus": false, "authorIds": [ 500, 501 ] }'),
    'expected': (200, ['100'])
  },
  {
    'name': 'get book (id=100)',
    'args': ('GET', '/lib-store/v1.0/books/100'),
    'expected': (200, ['book1', '999,', 'A-san', 'B-san', '1991-01-01', '1992-02-02'])
  },
  {
    'name': 'edit book title (id=100)',
    'args': ('PUT', '/lib-store/v1.0/books/100', '{ "title": "book1-edit" }'),
    'expected': (200, [])
  },
  {
    'name': 'create book (id=101)',
    'args': ('POST', '/lib-store/v1.0/books', '{ "id": 101, "title": "book2", "price": 1999, "publishedStatus": false, "authorIds": [ 500 ] }'),
    'expected': (200, ['101'])
  },
  {
    'name': 'get book and make sure title is updated (id=100)',
    'args': ('GET', '/lib-store/v1.0/books/100'),
    'expected': (200, ['book1-edit'])
  },
  {
    'name': 'get book (id=101) and make sure no change',
    'args': ('GET', '/lib-store/v1.0/books/101'),
    'expected': (200, ['book2', '1999,', 'A-san', '1991-01-01'])
  },
  {
    'name': 'get author (id=500)',
    'args': ('GET', '/lib-store/v1.0/authors/500'),
    'expected': (200, ['A-san', '1991-01-01'])
  },
  {
    'name': 'get books associated with author (id=500)',
    'args': ('GET', '/lib-store/v1.0/authors/500/books'),
    'expected': (200, ['book1-edit', 'book1'])
  },
  {
    'name': 'delete author (id=501) fails because there is associated book',
    'args': ('DELETE', '/lib-store/v1.0/authors/501'),
    'expected': (403, [])
  },
  {
    'name': 'remove author B from book1 authors',
    'args': ('PUT', '/lib-store/v1.0/books/100', '{"authorIds" : [ 500 ] }'),
    'expected': (200, [])
  },
  {
    'name': 'delete author (id=501) works',
    'args': ('DELETE', '/lib-store/v1.0/authors/501'),
    'expected': (200, [])
  },
  {
    'name': 'get deleted author (id=501)',
    'args': ('GET', '/lib-store/v1.0/authors/501'),
    'expected': (404, [])
  },
  {
    'name': 'get books associated with deleted author (id=501)',
    'args': ('GET', '/lib-store/v1.0/authors/501/books'),
    'expected': (200, ['[]'])
  },
  {
    'name': 'delete book (id=100)',
    'args': ('DELETE', '/lib-store/v1.0/books/100'),
    'expected': (200, [])
  },
  {
    'name': 'get book (id=101) and make sure no change',
    'args': ('GET', '/lib-store/v1.0/books/101'),
    'expected': (200, ['book2', '1999,', 'A-san', '1991-01-01'])
  },
  {
    'name': 'delete book (id=101)',
    'args': ('DELETE', '/lib-store/v1.0/books/101'),
    'expected': (200, [])
  },
  {
    'name': 'delete author (id=500)',
    'args': ('DELETE', '/lib-store/v1.0/authors/500'),
    'expected': (200, [])
  }
]

def run_predefined_call():
    for i, c in enumerate(CALLS):
        name = c['name']
        logging.info(f'TEST#{i} START - {name}')

        res = api_call(*c['args'])

        expected_status_code, expected_substr_lst = c['expected']
        actual_status_code = res.status_code

        logging.debug(f'code={actual_status_code}')
        logging.debug(f'resp_body={res.text}')

        assert actual_status_code == expected_status_code, \
          'except: %s, actual: %s' % (expected_status_code, actual_status_code)

        for expected_substr in expected_substr_lst:
            assert expected_substr in res.text, \
              'expected: includes %s, actual: not included (body = %s)' % (expected_substr, res.text)

        logging.info(f'TEST#{i} PASSED')
        logging.info(f'TEST#{i} END - {name}')

if __name__ == '__main__':
    logging.basicConfig(level=LOG_LEVEL)
    run_predefined_call()
