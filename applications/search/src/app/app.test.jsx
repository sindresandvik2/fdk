import React from 'react';
import { shallow } from 'enzyme';
import App from './app';

test('should render App correctly', () => {
  const wrapper = shallow(<App />);
  expect(wrapper).toMatchSnapshot();
});