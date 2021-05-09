import { TestBed, inject } from '@angular/core/testing';

import { ExtractesService } from './extractes.service';

describe('ExtractesService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ExtractesService]
    });
  });

  it('should be created', inject([ExtractesService], (service: ExtractesService) => {
    expect(service).toBeTruthy();
  }));
});
