import { TestBed } from '@angular/core/testing';

import { GithubTokenService } from './github-token.service';

describe('GithubTokenService', () => {
  let service: GithubTokenService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GithubTokenService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
