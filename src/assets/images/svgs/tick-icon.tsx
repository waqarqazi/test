import * as React from 'react';
import Svg, { Path } from 'react-native-svg';

function SvgComponent(props) {
  console.log("props",props);
  
  return (
    <Svg
      height={30}
      width={30}
      id="Layer_1"
      xmlns="http://www.w3.org/2000/svg"
      x="0px"
      y="0px"
      viewBox="0 0 45 45"
      xmlSpace="preserve"
      enableBackground="new 0 0 45 45"
      {...props}
    >
      <Path
        d="M42.6 5.1l-3.8-2.6c-1-.7-2.5-.4-3.2.6L17.1 30.5 8.5 22c-.9-.9-2.3-.9-3.2 0l-3.2 3.2c-.9.9-.9 2.3 0 3.2l13.1 13.1c.7.7 1.9 1.3 2.9 1.3s2.1-.7 2.8-1.6l22.3-33c.7-1 .4-2.4-.6-3.1z"
        fill={props?.fill}
      />
    </Svg>
  );
}

export default SvgComponent;
