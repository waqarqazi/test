import * as React from 'react';
import Svg, {Path} from 'react-native-svg';

function SvgComponent(props) {
  return (
    <Svg
      width={19}
      height={16}
      viewBox="0 0 19 16"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      {...props}>
      <Path
        d="M19 7H4.414l5.293-5.293L8.293.293.586 8l7.707 7.707 1.414-1.414L4.414 9H19V7z"
        fill={props.fill ? props.fill : '#000000'}
      />
    </Svg>
  );
}

export default SvgComponent;
