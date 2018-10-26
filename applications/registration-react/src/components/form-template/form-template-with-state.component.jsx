import { withState, withHandlers, compose } from 'recompose';
import { FormTemplate } from './form-template.component';

const enhance = compose(
  withState('collapse', 'toggleCollapse', ({ showInitially }) => showInitially),
  withHandlers({
    onToggle: props => () => {
      props.toggleCollapse(!props.collapse);
    }
  })
);

export const FormTemplateWithState = enhance(FormTemplate);
