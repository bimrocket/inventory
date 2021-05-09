import { ToSixDigitsPipe } from './to-six-digits.pipe';

describe('ToSixDigitsPipe', () => {
  it('create an instance', () => {
    const pipe = new ToSixDigitsPipe();
    expect(pipe).toBeTruthy();
  });
});
